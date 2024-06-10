package woowacourse.shopping.presentation.ui.shopping

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import woowacourse.shopping.data.local.mapper.toCartProduct
import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.domain.repository.CartItemRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.toRecentProduct
import woowacourse.shopping.presentation.base.BaseViewModel
import woowacourse.shopping.presentation.common.ErrorType
import woowacourse.shopping.presentation.common.EventState
import woowacourse.shopping.presentation.common.UpdateUiModel
import woowacourse.shopping.presentation.ui.shopping.model.ShoppingNavigation
import woowacourse.shopping.presentation.ui.shopping.model.ShoppingUiState

class ShoppingViewModel(
    private val productRepository: ProductRepository,
    private val cartItemRepository: CartItemRepository,
    private val recentProductRepository: RecentProductRepository,
) :
    BaseViewModel(), ShoppingActionHandler {
    private val _navigateHandler = MutableLiveData<EventState<ShoppingNavigation>>()
    val navigateHandler: LiveData<EventState<ShoppingNavigation>> get() = _navigateHandler

    private val _uiState = MutableLiveData<ShoppingUiState>(ShoppingUiState())
    val uiState: LiveData<ShoppingUiState> get() = _uiState

    init {
        loadCartProducts()
    }

    private fun loadCartProducts() =
        viewModelScope.launch {
            delay(500L) // Skeleton Loading
            val currentOffset = _uiState.value?.pageOffset ?: return@launch

            productRepository.getAllByPaging(currentOffset + 1, DEFAULT_PRODUCT_PAGE_SIZE).mapCatching {
                val carts = cartItemRepository.getAllByPaging(0, MAXIMUM_CART_SIZE).getOrNull()
                val cartCounts = cartItemRepository.getCount().getOrNull()
                val recentProducts = recentProductRepository.findAllByLimit(DEFAULT_RECENT_PAGE_SIZE).getOrNull()

                val currentState = _uiState.value ?: return@launch
                val cartProducts =
                    it.data.map { product ->
                        val cartItem = carts?.find { it.productId == product.productId }
                        if (cartItem != null) {
                            product.copy(quantity = cartItem.quantity, cartId = cartItem.cartId)
                        } else {
                            product
                        }
                    }
                _uiState.postValue(
                    currentState.copy(
                        cartProducts = cartProducts,
                        recentProduct = recentProducts ?: emptyList(),
                        pageOffset = it.offset,
                        isPageEnd = it.last,
                        cartTotalCount = cartCounts ?: 0,
                        isLoading = false,
                    ),
                )
            }
        }

    fun loadProductsByOffset() =
        viewModelScope.launch {
            val currentOffset = _uiState.value?.pageOffset ?: return@launch
            productRepository.getAllByPaging(currentOffset + 1, DEFAULT_PRODUCT_PAGE_SIZE).onSuccess {
                val currentState = _uiState.value ?: return@launch
                _uiState.postValue(
                    currentState.copy(
                        cartProducts = currentState.cartProducts + it.data,
                        pageOffset = it.offset,
                        isPageEnd = it.last,
                    ),
                )
            }.onFailure {
                showError(ErrorType.ERROR_PRODUCT_LOAD)
            }
        }

    fun loadCartItemCounts() =
        viewModelScope.launch {
            cartItemRepository.getCount().onSuccess { maxCount ->
                val currentState = uiState.value ?: return@launch
                _uiState.postValue(currentState.copy(cartTotalCount = maxCount))
            }.onFailure {
                showError(ErrorType.ERROR_CART_COUNT_LOAD)
            }
        }

    override fun onProductClick(cartProduct: CartProduct) {
        _navigateHandler.value = EventState(ShoppingNavigation.ToDetail(cartProduct))
    }

    override fun onRecentProductClick(recentProduct: RecentProduct) {
        _navigateHandler.value =
            EventState(
                ShoppingNavigation.ToDetail(
                    recentProduct.toCartProduct(),
                ),
            )
    }

    override fun onCartClick() {
        _navigateHandler.value = EventState(ShoppingNavigation.ToCart)
    }

    override fun loadProductMore() {
        loadProductsByOffset()
    }

    override fun onPlus(cartProduct: CartProduct) =
        viewModelScope.launch {
            val currentCartProducts = _uiState.value?.cartProducts?.map { it.copy() } ?: return@launch
            val index = currentCartProducts.indexOfFirst { it.productId == cartProduct.productId }
            currentCartProducts[index].plusQuantity()

            if (currentCartProducts[index].quantity == FIRST_UPDATE) {
                cartItemRepository.post(CartItemRequest.fromCartProduct(currentCartProducts[index]))
                    .onSuccess {
                        currentCartProducts[index].cartId = it.toLong()
                        saveRecentProduct(currentCartProducts[index])
                        val currentState = _uiState.value ?: return@launch
                        _uiState.postValue(
                            currentState.copy(
                                cartProducts = currentCartProducts,
                                cartTotalCount = currentState.cartTotalCount + 1,
                            ),
                        )
                    }
                    .onFailure {
                        showError(ErrorType.ERROR_PRODUCT_PLUS)
                    }
            } else {
                cartItemRepository.patch(
                    id = currentCartProducts[index].cartId.toInt(),
                    quantityRequestDto = QuantityRequest(quantity = currentCartProducts[index].quantity),
                )
                    .onSuccess {
                        saveRecentProduct(currentCartProducts[index])
                        val currentState = _uiState.value ?: return@launch
                        _uiState.postValue(
                            currentState.copy(
                                cartProducts = currentCartProducts,
                                cartTotalCount = currentState.cartTotalCount + 1,
                            ),
                        )
                    }
                    .onFailure {
                        showError(ErrorType.ERROR_PRODUCT_PLUS)
                    }
            }
        }

    override fun onMinus(cartProduct: CartProduct) =
        viewModelScope.launch {
            val currentCartProducts = _uiState.value?.cartProducts?.map { it.copy() } ?: return@launch
            val index = currentCartProducts.indexOfFirst { it.productId == cartProduct.productId }
            currentCartProducts[index].minusQuantity()

            if (currentCartProducts[index].quantity > 0) {
                cartItemRepository.patch(
                    id = currentCartProducts[index].cartId.toInt(),
                    quantityRequestDto = QuantityRequest(quantity = currentCartProducts[index].quantity),
                )
                    .onSuccess {
                        saveRecentProduct(currentCartProducts[index])
                        val currentState = _uiState.value ?: return@launch
                        _uiState.postValue(
                            currentState.copy(
                                cartProducts = currentCartProducts,
                                cartTotalCount = currentState.cartTotalCount - 1,
                            ),
                        )
                    }
                    .onFailure {
                        showError(ErrorType.ERROR_PRODUCT_MINUS)
                    }
            } else {
                cartItemRepository.delete(cartProduct.cartId.toInt()).onSuccess {
                    saveRecentProduct(currentCartProducts[index])
                    val currentState = _uiState.value ?: return@launch
                    _uiState.postValue(
                        currentState.copy(
                            cartProducts = currentCartProducts,
                            cartTotalCount = currentState.cartTotalCount - 1,
                        ),
                    )
                }.onFailure {
                    showError(ErrorType.ERROR_PRODUCT_MINUS)
                }
            }
        }

    fun updateCartProducts(updateUiModel: UpdateUiModel) {
        val currentState = _uiState.value ?: return
        val newCartProducts = currentState.cartProducts.map { it.copy() }
        updateUiModel.updatedItems.forEach { updatedItem ->
            val cartProductToUpdate = newCartProducts.find { it.productId == updatedItem.key }
            cartProductToUpdate?.quantity = updatedItem.value.quantity
            cartProductToUpdate?.cartId = updatedItem.value.cartId
        }
        _uiState.value = currentState.copy(cartProducts = newCartProducts)
    }

    fun loadAllRecent() =
        viewModelScope.launch {
            recentProductRepository.findAllByLimit(DEFAULT_RECENT_PAGE_SIZE).onSuccess {
                val currentState = _uiState.value ?: return@launch
                _uiState.postValue(
                    currentState.copy(
                        recentProduct = it,
                        isLoading = false,
                    ),
                )
            }.onFailure {
                showError(ErrorType.ERROR_RECENT_LOAD)
            }
        }

    override fun saveRecentProduct(cartProduct: CartProduct) =
        viewModelScope.launch {
            recentProductRepository.save(cartProduct.toRecentProduct()).onFailure {
                showError(ErrorType.ERROR_RECENT_INSERT)
            }
        }

    companion object {
        const val FIRST_UPDATE = 1
        const val DEFAULT_PRODUCT_PAGE_SIZE = 20
        const val DEFAULT_RECENT_PAGE_SIZE = 10
        const val MAXIMUM_CART_SIZE = 1000
    }
}
