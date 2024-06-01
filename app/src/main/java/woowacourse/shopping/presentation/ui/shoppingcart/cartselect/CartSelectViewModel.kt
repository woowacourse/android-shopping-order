package woowacourse.shopping.presentation.ui.shoppingcart.cartselect

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Product
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
        loadCartProducts(INIT_PAGE)
    }

    private fun loadCartProducts(page: Int) {
        thread {
            showLoading(loadingProvider = LoadingProvider.SKELETON_LOADING)
            Thread.sleep(1000) // TODO 스켈레톤 UI를 보여주기 위한 sleep..zzz

            shoppingCartPagingSource.load(page).onSuccess { pagingCartProduct ->
                hideError()
                loadedCartProducts(pagingCartProduct)
            }.onFailure { e ->
                showError(e)
                showMessage(MessageProvider.DefaultErrorMessage)
            }

            hideLoading()
        }
    }

    private fun loadedCartProducts(pagingCartProduct: PagingCartProduct) {
        val state = uiState.value ?: return
        val carts = shoppingRepository.getAllCarts().getOrNull()

        val cartIdList = state.orderCarts.map { it.id }

        val newCartList =
            pagingCartProduct.cartProducts.map { cart ->
                if (cart.cart.id in cartIdList) {
                    cart.copy(isChecked = true)
                } else {
                    cart.copy(isChecked = false)
                }
            }

        val newPagingCartProduct = pagingCartProduct.copy(cartProducts = newCartList)
        val totalElements = carts?.totalElements ?: 0

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
        var state = uiState.value ?: return
        val updatedCartProducts =
            state.pagingCartProduct.cartProducts.map { cartProduct ->
                if (cartProduct.cart.product.id == productId) {
                    val updateProduct = updateProduct(cartProduct, increment)
                    val orderCartIds = state.orderCarts.map { it.product.id }
                    if (productId in orderCartIds) {
                        state = state.copy(orderCarts = state.orderCarts - cartProduct.cart)
                        state = state.copy(orderCarts = state.orderCarts + updateProduct.cart)
                    }
                    updateProduct
                } else {
                    cartProduct
                }
            }
        val pagingCartProduct =
            PagingCartProduct(
                cartProducts = updatedCartProducts,
                currentPage = state.pagingCartProduct.currentPage,
                last = state.pagingCartProduct.last,
            )

        _uiState.value = state.copy(pagingCartProduct = pagingCartProduct)
    }

    private fun updateProduct(
        cartProduct: CartProduct,
        increment: Boolean,
    ): CartProduct {
        val updatedQuantity = calculateUpdatedQuantity(cartProduct.cart.quantity, increment)
        calculatedUpdateCart(cartProduct.cart, updatedQuantity)
        return cartProduct.copy(cart = cartProduct.cart.copy(quantity = updatedQuantity))
    }

    private fun calculateUpdatedQuantity(
        currentQuantity: Int,
        increment: Boolean,
    ): Int {
        return if (increment) currentQuantity + 1 else currentQuantity - 1
    }

    private fun calculatedUpdateCart(
        cart: Cart,
        updatedQuantity: Int,
    ) {
        if (cart.quantity == Cart.INIT_QUANTITY_NUM) {
            insertCartProduct(cart.product, updatedQuantity)
        } else if (updatedQuantity == Cart.INIT_QUANTITY_NUM) {
            deleteCartProduct(cart.id)
        } else {
            updateCartProduct(cart.id, updatedQuantity)
        }
    }

    private fun insertCartProduct(
        product: Product,
        quantity: Int,
    ) {
        thread {
            shoppingRepository.postCartItem(
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
        thread {
            shoppingRepository.deleteCartItem(cartId = cartId).onSuccess {
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

    override fun checkCartProduct(cartProduct: CartProduct) {
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

    private fun updateCartProduct(
        cartId: Int,
        quantity: Int,
    ) {
        thread {
            shoppingRepository.patchCartItem(
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
                CartSelectNavigateAction.NavigateToRecommend(orderCartProducts = state.orderCarts),
            )
        }
    }

    fun showPageIndicator(): Boolean {
        val state = uiState.value ?: return false
        return if (loading.value == LoadingProvider.SKELETON_LOADING) {
            false
        } else {
            !(state.pagingCartProduct.currentPage == 0 && state.pagingCartProduct.last)
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
