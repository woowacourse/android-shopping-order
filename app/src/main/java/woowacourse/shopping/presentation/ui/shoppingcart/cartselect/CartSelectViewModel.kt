package woowacourse.shopping.presentation.ui.shoppingcart.cartselect

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.presentation.base.BaseViewModel
import woowacourse.shopping.presentation.base.BaseViewModelFactory
import woowacourse.shopping.presentation.base.Event
import woowacourse.shopping.presentation.base.MessageProvider
import woowacourse.shopping.presentation.base.emit
import woowacourse.shopping.presentation.common.ProductCountHandler
import woowacourse.shopping.presentation.ui.shoppingcart.cartselect.adapter.ShoppingCartPagingSource

class CartSelectViewModel(
    private val shoppingRepository: ShoppingCartRepository,
) :
    BaseViewModel(),
        CartSelectActionHandler,
        ProductCountHandler {
    private val _uiState: MutableLiveData<CartSelectUiState> =
        MutableLiveData(CartSelectUiState())
    val uiState: LiveData<CartSelectUiState> get() = _uiState

    private val _navigateAction: MutableLiveData<Event<CartSelectNavigateAction>> =
        MutableLiveData(null)
    val navigateAction: LiveData<Event<CartSelectNavigateAction>> get() = _navigateAction

    private val shoppingCartPagingSource = ShoppingCartPagingSource(shoppingRepository)

    init {
        loadCartProducts(INIT_PAGE)
    }

    private fun loadCartProducts(page: Int) {
        viewModelScope.launch {
            showLoading()

            shoppingCartPagingSource.load(page).onSuccess { pagingCartProduct ->
                hideError()

                val carts = shoppingRepository.getAllCarts().getOrNull()

                val cartIdList = uiState.value?.orderCartList?.values?.map { it.id } ?: emptyList()

                val newCartList =
                    pagingCartProduct.cartList.map { cart ->
                        if (cart.id in cartIdList) {
                            cart.copy(isChecked = true)
                        } else {
                            cart.copy(isChecked = false)
                        }
                    }

                val newPagingCartProduct = pagingCartProduct.copy(cartList = newCartList)

                _uiState.value?.let { state ->
                    _uiState.value =
                        state.copy(
                            pagingCartProduct = newPagingCartProduct,
                            totalElements = carts?.totalElements ?: 0,
                        )
                }
            }.onFailure { e ->
                showError(e)
                showMessage(MessageProvider.DefaultErrorMessage)
            }

            hideLoading()
        }
    }

    override fun retry() {
        _uiState.value?.let { state ->
            loadCartProducts(state.pagingCartProduct.currentPage)
        }
    }

    override fun plusProductQuantity(
        productId: Long,
        position: Int,
    ) {
        updateProductQuantity(productId = productId, increment = true)
    }

    override fun minusProductQuantity(
        productId: Long,
        position: Int,
    ) {
        updateProductQuantity(productId = productId, increment = false)
    }

    private fun updateProductQuantity(
        productId: Long,
        increment: Boolean,
    ) {
        _uiState.value?.let { state ->
            val updatedProductList =
                state.pagingCartProduct.cartList.map { cart ->
                    if (cart.product.id == productId) {
                        val updateProduct = cart.updateProduct(increment)
                        state.orderCartList[updateProduct.id] = updateProduct
                        updateProduct
                    } else {
                        cart
                    }
                }
            val pagingCartProduct =
                PagingCartProduct(
                    cartList = updatedProductList,
                    currentPage = state.pagingCartProduct.currentPage,
                    last = state.pagingCartProduct.last,
                )
            _uiState.postValue(
                state.copy(pagingCartProduct = pagingCartProduct),
            )
        }
    }

    private fun Cart.updateProduct(increment: Boolean): Cart {
        val updatedQuantity = if (increment) this.quantity + 1 else this.quantity - 1
        when {
            this.quantity == 0 -> insertCartProduct(this.product, updatedQuantity)
            updatedQuantity == 0 -> deleteCartProduct(this.id)
            else -> updateCartProduct(this.id, updatedQuantity)
        }
        return this.copy(quantity = updatedQuantity)
    }

    private fun insertCartProduct(
        product: Product,
        quantity: Int,
    ) {
        viewModelScope.launch {
            shoppingRepository.insertCartProduct(
                productId = product.id,
                quantity = quantity,
            ).onSuccess {
                hideError()
            }.onFailure { e ->
                showError(e)
            }
        }
    }

    override fun deleteCartProduct(cartId: Int) {
        viewModelScope.launch {
            shoppingRepository.deleteCartProductById(cartId = cartId).onSuccess {
                uiState.value?.let { state ->
                    loadCartProducts(state.pagingCartProduct.currentPage)
                }
            }.onSuccess {
                hideError()
            }.onFailure { e ->
                showError(e)
                showMessage(MessageProvider.DefaultErrorMessage)
            }
        }
    }

    override fun checkCartProduct(cart: Cart) {
        _uiState.value?.let { state ->
            if (cart.isChecked) {
                state.orderCartList.remove(cart.id)
                _uiState.value = state.copy(orderCartList = state.orderCartList)
            } else {
                state.orderCartList[cart.id] = cart.copy(isChecked = true)
                _uiState.value = state.copy(orderCartList = state.orderCartList)
            }

            val newPagingCartProduct =
                state.pagingCartProduct.cartList.map { cart ->
                    if (cart.id in state.orderCartList.map { it.key }) {
                        cart.copy(isChecked = true)
                    } else {
                        cart.copy(isChecked = false)
                    }
                }

            _uiState.postValue(
                state.copy(
                    pagingCartProduct = state.pagingCartProduct.copy(cartList = newPagingCartProduct),
                ),
            )
        }
    }

    override fun checkAllCartProduct() {
        viewModelScope.launch {
            shoppingRepository.getAllCarts().onSuccess { carts ->
                hideError()

                if (carts.totalElements == _uiState.value?.orderCartList?.size) {
                    _uiState.value?.let { state ->

                        val newPagingCartProduct =
                            state.pagingCartProduct.cartList.map { cart ->
                                cart.copy(isChecked = false)
                            }

                        _uiState.value =
                            state.copy(
                                orderCartList = mutableMapOf(),
                                pagingCartProduct = state.pagingCartProduct.copy(cartList = newPagingCartProduct),
                            )
                    }
                } else {
                    _uiState.value?.let { state ->
                        val newPagingCartProduct =
                            state.pagingCartProduct.cartList.map { cart ->
                                cart.copy(isChecked = true)
                            }

                        val orderCartList =
                            carts.content.associateBy { cart -> cart.id }.toMutableMap()

                        _uiState.value =
                            state.copy(
                                orderCartList = orderCartList,
                                pagingCartProduct = state.pagingCartProduct.copy(cartList = newPagingCartProduct),
                            )
                    }
                }
            }.onFailure { e ->
                showError(e)
            }
        }
    }

    private fun updateCartProduct(
        cartId: Int,
        quantity: Int,
    ) {
        viewModelScope.launch {
            shoppingRepository.updateCartProduct(
                cartId = cartId,
                quantity = quantity,
            ).onSuccess {
                hideError()
            }.onFailure { e ->
                showError(e)
            }
        }
    }

    fun loadNextPage() {
        uiState.value?.let { state ->
            loadCartProducts(state.pagingCartProduct.currentPage + 1)
        }
    }

    fun loadPreviousPage() {
        uiState.value?.let { state ->
            loadCartProducts(state.pagingCartProduct.currentPage - 1)
        }
    }

    fun navigateToRecommend() {
        _uiState.value?.let { state ->
            _navigateAction.emit(
                CartSelectNavigateAction.NavigateToRecommend(orderCarts = state.orderCartList.values.toList()),
            )
        }
    }

    companion object {
        const val INIT_PAGE = 0

        fun factory(shoppingCartRepository: ShoppingCartRepository): ViewModelProvider.Factory {
            return BaseViewModelFactory {
                CartSelectViewModel(
                    shoppingCartRepository,
                )
            }
        }
    }
}
