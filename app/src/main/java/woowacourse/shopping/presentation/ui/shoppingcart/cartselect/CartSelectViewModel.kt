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
    private val _uiState: MutableLiveData<CartSelectUiState> = MutableLiveData(CartSelectUiState())
    val uiState: LiveData<CartSelectUiState> get() = _uiState

    private val _navigateAction: MutableLiveData<Event<CartSelectNavigateAction>> =
        MutableLiveData(null)
    val navigateAction: LiveData<Event<CartSelectNavigateAction>> get() = _navigateAction

    private val shoppingCartPagingSource = ShoppingCartPagingSource(shoppingRepository)

    init {
        loadCartProducts(INIT_PAGE)
    }

    override fun retry() {
        _uiState.value?.let { state ->
            loadCartProducts(state.pagingCartProduct.currentPage)
        }
    }

    fun loadNextPage() {
        uiState.value?.let { state ->
            loadCartProducts(state.pagingCartProduct.currentPage + PAGE_STEP)
        }
    }

    fun loadPreviousPage() {
        uiState.value?.let { state ->
            loadCartProducts(state.pagingCartProduct.currentPage - PAGE_STEP)
        }
    }

    fun navigateToRecommend() {
        _uiState.value?.let { state ->
            _navigateAction.emit(
                CartSelectNavigateAction.NavigateToRecommend(orderCarts = state.orderCarts.values.toList()),
            )
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

    override fun deleteCartProduct(cartId: Int) {
        viewModelScope.launch {
            shoppingRepository.deleteCartProductById(cartId = cartId).onSuccess {
                hideError()

                uiState.value?.let { state ->
                    loadCartProducts(state.pagingCartProduct.currentPage)
                }
            }.onFailure { e ->
                showError(e)
                showMessage(MessageProvider.DefaultErrorMessage)
            }
        }
    }

    override fun checkCartProduct(cart: Cart) {
        _uiState.value?.let { state ->
            if (cart.isChecked) {
                state.orderCarts.remove(cart.id)
                _uiState.value = state.copy(orderCarts = state.orderCarts)
            } else {
                state.orderCarts[cart.id] = cart.copy(isChecked = true)
                _uiState.value = state.copy(orderCarts = state.orderCarts)
            }

            val newPagingCarts =
                state.pagingCartProduct.carts.map { cart ->
                    cart.copy(isChecked = cart.id in state.orderCarts.map { it.key })
                }

            _uiState.value =
                state.copy(pagingCartProduct = state.pagingCartProduct.copy(carts = newPagingCarts))
        }
    }

    override fun checkAllCartProduct() {
        viewModelScope.launch {
            shoppingRepository.getAllCarts().onSuccess { carts ->
                hideError()

                if (carts.totalElements == _uiState.value?.orderCarts?.size) {
                    _uiState.value?.let { state ->

                        val newPagingCartProduct = state.pagingCartProduct.carts.map { cart ->
                            cart.copy(isChecked = false)
                        }

                        _uiState.value = state.copy(
                            orderCarts = hashMapOf(),
                            pagingCartProduct = state.pagingCartProduct.copy(carts = newPagingCartProduct),
                        )
                    }
                } else {
                    _uiState.value?.let { state ->
                        val newPagingCarts = state.pagingCartProduct.carts.map { cart ->
                            cart.copy(isChecked = true)
                        }

                        val orderCarts =
                            carts.content.associateBy { cart -> cart.id }.toMutableMap()

                        _uiState.value = state.copy(
                            orderCarts = orderCarts,
                            pagingCartProduct = state.pagingCartProduct.copy(carts = newPagingCarts),
                        )
                    }
                }
            }.onFailure { e ->
                showError(e)
            }
        }
    }

    private fun loadCartProducts(page: Int) {
        viewModelScope.launch {
            showLoading()

            shoppingCartPagingSource.load(page).onSuccess { pagingCartProduct ->
                hideError()

                val allCarts = shoppingRepository.getAllCarts().getOrNull()
                val orderCartsId =
                    uiState.value?.orderCarts?.values?.map { it.id } ?: emptyList()
                val pagingCarts =
                    pagingCartProduct.carts.map { pagingCart ->
                        pagingCart.copy(isChecked = pagingCart.id in orderCartsId)
                    }

                _uiState.value?.let { state ->
                    _uiState.value =
                        state.copy(
                            pagingCartProduct = pagingCartProduct.copy(carts = pagingCarts),
                            totalElements = allCarts?.totalElements,
                        )
                }
            }.onFailure { e ->
                showError(e)
                showMessage(MessageProvider.DefaultErrorMessage)
            }

            hideLoading()
        }
    }

    private fun updateProductQuantity(
        productId: Long,
        increment: Boolean,
    ) {
        _uiState.value?.let { state ->
            val carts =
                state.pagingCartProduct.carts.map { cart ->
                    if (cart.product.id == productId) {
                        cart.updateProduct(increment)
                    } else {
                        cart
                    }
                }
            val pagingCartProduct =
                PagingCartProduct(
                    carts = carts,
                    currentPage = state.pagingCartProduct.currentPage,
                    last = state.pagingCartProduct.last,
                )

            _uiState.value = state.copy(pagingCartProduct = pagingCartProduct)
        }
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

    private fun Cart.updateProduct(increment: Boolean): Cart {
        val updatedQuantity = if (increment) this.quantity + 1 else this.quantity - 1
        when {
            this.quantity == 0 -> insertCartProduct(this.product, updatedQuantity)
            updatedQuantity == 0 -> deleteCartProduct(this.id)
            else -> updateCartProduct(this.id, updatedQuantity)
        }
        return this.copy(quantity = updatedQuantity)
    }

    companion object {
        const val INIT_PAGE = 0
        const val PAGE_STEP = 1

        fun factory(shoppingCartRepository: ShoppingCartRepository): ViewModelProvider.Factory {
            return BaseViewModelFactory {
                CartSelectViewModel(
                    shoppingCartRepository,
                )
            }
        }
    }
}
