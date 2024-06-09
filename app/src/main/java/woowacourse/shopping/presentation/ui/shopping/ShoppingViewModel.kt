package woowacourse.shopping.presentation.ui.shopping

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.local.mapper.toCartProduct
import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.data.remote.paging.LoadResult
import woowacourse.shopping.data.remote.paging.mergeWith
import woowacourse.shopping.domain.CartItemRepository
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.ProductRepository
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.domain.RecentProductRepository
import woowacourse.shopping.domain.toRecentProduct
import woowacourse.shopping.presentation.ErrorType
import woowacourse.shopping.presentation.ui.EventState
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.UpdateUiModel
import woowacourse.shopping.presentation.ui.shopping.model.NavigateUiState

class ShoppingViewModel(
    private val productRepository: ProductRepository,
    private val cartItemRepository: CartItemRepository,
    private val recentProductRepository: RecentProductRepository,
) :
    ViewModel(), ShoppingActionHandler {
    private val _cartCount = MutableLiveData<Int>(0)
    val cartCount: LiveData<Int> get() = _cartCount

    private val _recentProducts = MutableLiveData<UiState<List<RecentProduct>>>(UiState.Loading)
    val recentProducts: LiveData<UiState<List<RecentProduct>>> get() = _recentProducts

    private val _errorHandler = MutableLiveData<EventState<ErrorType>>()
    val errorHandler: LiveData<EventState<ErrorType>> get() = _errorHandler

    private val _navigateHandler = MutableLiveData<EventState<NavigateUiState>>()
    val navigateHandler: LiveData<EventState<NavigateUiState>> get() = _navigateHandler

    private val _products = MutableLiveData<UiState<LoadResult.Page<CartProduct>>>(UiState.Loading)
    val products: LiveData<UiState<LoadResult.Page<CartProduct>>> get() = _products

    private val _carts = MutableLiveData<UiState<List<CartProduct>>>(UiState.Loading)
    val carts: LiveData<UiState<List<CartProduct>>> get() = _carts

    val cartProducts = MediatorLiveData<UiState<List<CartProduct>>>(UiState.Loading)

    init {
        cartProducts.addSource(_products) {
            combineCartProducts()
        }
        cartProducts.addSource(_carts) {
            combineCartProducts()
        }
        getCartItemCounts()
    }

    private fun combineCartProducts() {
        if (_products.value is UiState.Success && _carts.value is UiState.Success) {
            val products = (_products.value as UiState.Success).data
            val carts = (_carts.value as UiState.Success).data

            // cartProducts 리스트 생성
            val cartProducts =
                products.data.map { product ->
                    val cartItem = carts.find { it.productId == product.productId }
                    if (cartItem != null) {
                        product.copy(quantity = cartItem.quantity, cartId = cartItem.cartId)
                    } else {
                        product
                    }
                }
            this.cartProducts.value = UiState.Success(cartProducts)
        }
    }

    fun loadProductsByOffset() =
        viewModelScope.launch {
            val offset = if (_products.value is UiState.Success) (_products.value as UiState.Success).data.offset + 1 else 0
            productRepository.getProductsByPaging(offset, DEFAULT_PAGE_SIZE).onSuccess {
                if (_products.value is UiState.Loading) {
                    _products.postValue(UiState.Success(it))
                } else {
                    _products.postValue(
                        UiState.Success((_products.value as UiState.Success).data.mergeWith(it)),
                    )
                }
            }.onFailure {
                _errorHandler.postValue(EventState(ErrorType.ERROR_PRODUCT_LOAD))
            }
        }

    fun loadAllCart() =
        viewModelScope.launch {
            cartItemRepository.getCartItems(0, 5000).onSuccess {
                _carts.postValue(UiState.Success(it))
            }.onFailure {
                _errorHandler.value = EventState(ErrorType.ERROR_CART_LOAD)
            }
        }

    fun getCartItemCounts() =
        viewModelScope.launch {
            cartItemRepository.getCartItemsCounts().onSuccess { maxCount ->
                _cartCount.postValue(maxCount)
            }.onFailure {
                _errorHandler.postValue(EventState(ErrorType.ERROR_CART_COUNT_LOAD))
            }
        }

    override fun onProductClick(cartProduct: CartProduct) {
        _navigateHandler.value = EventState(NavigateUiState.ToDetail(cartProduct))
    }

    override fun onRecentProductClick(recentProduct: RecentProduct) {
        _navigateHandler.value =
            EventState(
                NavigateUiState.ToDetail(
                    recentProduct.toCartProduct(),
                ),
            )
    }

    override fun onCartClick() {
        _navigateHandler.value = EventState(NavigateUiState.ToCart)
    }

    override fun loadMore() {
        loadProductsByOffset()
    }

    override fun onPlus(cartProduct: CartProduct) =
        viewModelScope.launch {
            val cartProducts = (this@ShoppingViewModel.cartProducts.value as UiState.Success).data.map { it.copy() }
            val index = cartProducts.indexOfFirst { it.productId == cartProduct.productId }

            cartProducts[index].plusQuantity()

            if (cartProducts[index].quantity == FIRST_UPDATE) {
                cartItemRepository.postCartItem(CartItemRequest.fromCartProduct(cartProducts[index]))
                    .onSuccess {
                        cartProducts[index].cartId = it.toLong()
                        saveRecentProduct(cartProducts[index])
                        this@ShoppingViewModel.cartProducts.postValue(UiState.Success(cartProducts))
                        _cartCount.postValue(_cartCount.value?.plus(1))
                    }
                    .onFailure {
                        _errorHandler.postValue(EventState(ErrorType.ERROR_PRODUCT_PLUS))
                    }
            } else {
                cartItemRepository.patchCartItem(
                    id = cartProducts[index].cartId.toInt(),
                    quantityRequestDto = QuantityRequest(quantity = cartProducts[index].quantity),
                )
                    .onSuccess {
                        saveRecentProduct(cartProducts[index])
                        this@ShoppingViewModel.cartProducts.postValue(UiState.Success(cartProducts))
                        _cartCount.postValue(_cartCount.value?.plus(1))
                    }
                    .onFailure {
                        _errorHandler.postValue(EventState(ErrorType.ERROR_PRODUCT_PLUS))
                    }
            }
        }

    override fun onMinus(cartProduct: CartProduct) =
        viewModelScope.launch {
            val cartProducts = (this@ShoppingViewModel.cartProducts.value as UiState.Success).data.map { it.copy() }
            val index = cartProducts.indexOfFirst { it.productId == cartProduct.productId }
            cartProducts[index].minusQuantity()

            if (cartProducts[index].quantity > 0) {
                cartItemRepository.patchCartItem(
                    id = cartProducts[index].cartId.toInt(),
                    quantityRequestDto = QuantityRequest(quantity = cartProducts[index].quantity),
                )
                    .onSuccess {
                        this@ShoppingViewModel.cartProducts.postValue(UiState.Success(cartProducts))
                        saveRecentProduct(cartProducts[index])
                        _cartCount.postValue(_cartCount.value?.minus(1))
                    }
                    .onFailure {
                        _errorHandler.postValue(EventState(ErrorType.ERROR_PRODUCT_MINUS))
                    }
            } else {
                cartItemRepository.deleteCartItem(cartProduct.cartId.toInt()).onSuccess {
                    this@ShoppingViewModel.cartProducts.postValue(UiState.Success(cartProducts))
                    saveRecentProduct(cartProducts[index])
                    _cartCount.postValue(_cartCount.value?.minus(1))
                }.onFailure {
                    _errorHandler.postValue(EventState(ErrorType.ERROR_PRODUCT_MINUS))
                }
            }
        }

    fun updateCartProducts(updateUiModel: UpdateUiModel) {
        val cartProducts = (this.cartProducts.value as UiState.Success).data.map { it.copy() }
        updateUiModel.updatedItems.forEach { updatedItem ->
            val cartProductToUpdate = cartProducts.find { it.productId == updatedItem.key }
            cartProductToUpdate?.quantity = updatedItem.value.quantity
            cartProductToUpdate?.cartId = updatedItem.value.cartId
        }
        this.cartProducts.value = UiState.Success(cartProducts)
    }

    fun findAllRecent() =
        viewModelScope.launch {
            recentProductRepository.findAllByLimit(10).onSuccess {
                _recentProducts.postValue(UiState.Success(it))
            }.onFailure {
                _errorHandler.postValue(EventState(ErrorType.ERROR_RECENT_LOAD))
            }
        }

    override fun saveRecentProduct(cartProduct: CartProduct) =
        viewModelScope.launch {
            recentProductRepository.save(cartProduct.toRecentProduct()).onFailure {
                _errorHandler.postValue(EventState(ErrorType.ERROR_RECENT_INSERT))
            }
        }

    companion object {
        const val FIRST_UPDATE = 1
        const val DEFAULT_PAGE_SIZE = 20
    }
}
