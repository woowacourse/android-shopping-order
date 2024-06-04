package woowacourse.shopping.presentation.ui.shoppingcart.cartselect

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.presentation.base.BaseViewModel
import woowacourse.shopping.presentation.base.BaseViewModelFactory
import woowacourse.shopping.presentation.base.Event
import woowacourse.shopping.presentation.base.LoadingProvider
import woowacourse.shopping.presentation.base.MessageProvider
import woowacourse.shopping.presentation.base.emit
import woowacourse.shopping.presentation.common.ProductCountHandler
import woowacourse.shopping.presentation.ui.shoppingcart.cartselect.adapter.ShoppingCartPagingSource
import kotlin.concurrent.thread

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
        thread {
            showLoading(loadingProvider = LoadingProvider.SKELETON_LOADING)
            loadCartProducts(INIT_PAGE)
            Thread.sleep(1000)
            hideLoading()
        }
    }

    private fun loadCartProducts(page: Int) {
        thread {
            shoppingCartPagingSource.load(page).onSuccess { pagingCartProduct ->
                hideError()
                loadedCartProducts(pagingCartProduct)
            }.onFailure { e ->
                showError(e)
                showMessage(MessageProvider.DefaultErrorMessage)
            }
        }
    }

    private fun loadedCartProducts(pagingCartProduct: PagingCartProduct) {
        val state = uiState.value ?: return
        val cartIdList = state.orderCarts.map { it.id }

        val cartProducts =
            pagingCartProduct.cartProducts.map { cart ->
                if (cart.cart.id in cartIdList) {
                    cart.copy(isChecked = true)
                } else {
                    cart.copy(isChecked = false)
                }
            }

        val newPagingCartProduct = pagingCartProduct.copy(cartProducts = cartProducts)
        val totalElements = shoppingRepository.getAllCarts().getOrNull()?.totalElements ?: 0

        _uiState.postValue(
            state.copy(
                pagingCartProduct = newPagingCartProduct,
                totalElements = totalElements,
            ),
        )
    }

    override fun retry() {
        uiState.value?.let { state ->
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
        val state = uiState.value ?: return

        val updateCartProducts =
            state.pagingCartProduct.cartProducts.map { cartProduct ->
                if (cartProduct.cart.product.id == productId) {
                    val quantity = calculateUpdatedQuantity(cartProduct.cart.quantity, increment)
                    calculatedUpdateCart(cartProduct, quantity)
                    cartProduct.copy(cart = cartProduct.cart.copy(quantity = quantity))
                } else {
                    cartProduct
                }
            }
        _uiState.value = state.copy(pagingCartProduct = PagingCartProduct(updateCartProducts))
    }

    private fun calculateUpdatedQuantity(
        currentQuantity: Int,
        increment: Boolean,
    ): Int {
        return if (increment) currentQuantity + 1 else currentQuantity - 1
    }

    private fun calculatedUpdateCart(
        cartProduct: CartProduct,
        updatedQuantity: Int,
    ) {
        if (updatedQuantity == Cart.INIT_QUANTITY_NUM) {
            deleteCartProduct(cartProduct)
        } else {
            updateCartProduct(cartProduct, updatedQuantity)
        }
    }

    private fun containsProductIdInOrder(
        productId: Long,
        orderCartIds: List<Long>,
    ): Boolean = productId in orderCartIds

    override fun deleteCartProduct(cartProduct: CartProduct) {
        thread {
            shoppingRepository.deleteCartItem(cartId = cartProduct.cart.id).onSuccess {
                val state = uiState.value ?: return@onSuccess
                loadCartProducts(state.pagingCartProduct.currentPage)
            }.onSuccess {
                hideError()
                var state = _uiState.value ?: return@onSuccess
                val orderCartIds = state.orderCarts.map { it.product.id }
                if (containsProductIdInOrder(cartProduct.cart.product.id, orderCartIds)) {
                    state = state.copy(orderCarts = state.orderCarts - cartProduct.cart)
                }
                _uiState.postValue(state)
            }.onFailure { e ->
                showError(e)
                showMessage(MessageProvider.DefaultErrorMessage)
            }
        }
    }

    private fun updateCartProduct(
        cartProduct: CartProduct,
        quantity: Int,
    ) {
        thread {
            shoppingRepository.patchCartItem(
                cartId = cartProduct.cart.id,
                quantity = quantity,
            ).onSuccess {
                hideError()
                var state = _uiState.value ?: return@onSuccess
                val orderCartIds = state.orderCarts.map { it.product.id }
                if (containsProductIdInOrder(cartProduct.cart.product.id, orderCartIds)) {
                    state = state.copy(orderCarts = state.orderCarts - cartProduct.cart)
                    state =
                        state.copy(orderCarts = state.orderCarts + cartProduct.cart.copy(quantity = quantity))
                }
                _uiState.postValue(state)
            }.onFailure { e ->
                showError(e)
            }
        }
    }

    override fun updateCheckState(cartProduct: CartProduct) {
        var state = uiState.value ?: return
        state =
            if (cartProduct.isChecked) {
                state.copy(orderCarts = state.orderCarts - cartProduct.cart)
            } else {
                state.copy(orderCarts = state.orderCarts + cartProduct.cart)
            }

        val cartIds = state.orderCarts.map { it.id }
        val newPagingCartProduct =
            state.pagingCartProduct.cartProducts.map { newCartProduct ->
                if (newCartProduct.cart.id in cartIds) {
                    newCartProduct.copy(isChecked = true)
                } else {
                    newCartProduct.copy(isChecked = false)
                }
            }

        _uiState.value =
            state.copy(pagingCartProduct = state.pagingCartProduct.copy(cartProducts = newPagingCartProduct))
    }

    override fun checkAllCartProduct() {
        thread {
            shoppingRepository.getAllCarts().onSuccess { carts ->
                hideError()
                val state = uiState.value ?: return@onSuccess

                val newPagingCartProduct =
                    state.pagingCartProduct.cartProducts.map { cartProduct ->
                        cartProduct.copy(isChecked = !state.isAllChecked)
                    }

                val orderCarts = if (state.isAllChecked) emptyList() else carts.content

                _uiState.postValue(
                    state.copy(
                        orderCarts = orderCarts,
                        pagingCartProduct = state.pagingCartProduct.copy(cartProducts = newPagingCartProduct),
                    ),
                )
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
                CartSelectNavigateAction.NavigateToRecommend(orderCartProducts = state.orderCarts),
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
