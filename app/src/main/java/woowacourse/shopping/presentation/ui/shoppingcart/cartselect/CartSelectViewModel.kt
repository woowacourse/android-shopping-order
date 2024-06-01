package woowacourse.shopping.presentation.ui.shoppingcart.cartselect

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
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

                val carts = shoppingRepository.getAllCarts().getOrNull()

                val cartIdList =
                    uiState.value?.orderCarts?.map { it.id } ?: emptyList()

                val newCartList =
                    pagingCartProduct.cartList.map { cart ->
                        if (cart.cart.id in cartIdList) {
                            cart.copy(isChecked = true)
                        } else {
                            cart.copy(isChecked = false)
                        }
                    }

                val newPagingCartProduct = pagingCartProduct.copy(cartList = newCartList)

                _uiState.value?.let { state ->
                    _uiState.postValue(
                        state.copy(
                            pagingCartProduct = newPagingCartProduct,
                            totalElements = carts?.totalElements ?: 0,
                        ),
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
        var state = _uiState.value ?: return
        val updatedProductList =
            state.pagingCartProduct.cartList.map { cartProduct ->
                if (cartProduct.cart.product.id == productId) {
                    val updateProduct = cartProduct.updateProduct(increment)
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
                cartList = updatedProductList,
                currentPage = state.pagingCartProduct.currentPage,
                last = state.pagingCartProduct.last,
            )
        _uiState.postValue(
            state.copy(pagingCartProduct = pagingCartProduct),
        )
    }

    private fun CartProduct.updateProduct(increment: Boolean): CartProduct {
        val updatedQuantity = if (increment) this.cart.quantity + 1 else this.cart.quantity - 1
        when {
            this.cart.quantity == 0 -> insertCartProduct(this.cart.product, updatedQuantity)
            updatedQuantity == 0 -> deleteCartProduct(this.cart.id)
            else -> updateCartProduct(this.cart.id, updatedQuantity)
        }
        return this.copy(cart = this.cart.copy(quantity = updatedQuantity))
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
        var state = _uiState.value ?: return
        state =
            if (cartProduct.isChecked) {
                state.copy(orderCarts = state.orderCarts - cartProduct.cart)
            } else {
                state.copy(orderCarts = state.orderCarts + cartProduct.cart)
            }

        val cartIds = state.orderCarts.map { it.id }
        val newPagingCartProduct =
            state.pagingCartProduct.cartList.map { newCartProduct ->
                if (newCartProduct.cart.id in cartIds) {
                    newCartProduct.copy(isChecked = true)
                } else {
                    newCartProduct.copy(isChecked = false)
                }
            }

        _uiState.value =
            state.copy(pagingCartProduct = state.pagingCartProduct.copy(cartList = newPagingCartProduct))
    }

    override fun checkAllCartProduct() {
        thread {
            shoppingRepository.getAllCarts().onSuccess { carts ->
                hideError()

                if (carts.totalElements == _uiState.value?.orderCarts?.size) {
                    _uiState.value?.let { state ->

                        val newPagingCartProduct =
                            state.pagingCartProduct.cartList.map { cartProduct ->
                                cartProduct.copy(isChecked = false)
                            }

                        _uiState.postValue(
                            state.copy(
                                orderCarts = emptyList(),
                                pagingCartProduct = state.pagingCartProduct.copy(cartList = newPagingCartProduct),
                            ),
                        )
                    }
                } else {
                    _uiState.value?.let { state ->
                        val newPagingCartProduct =
                            state.pagingCartProduct.cartList.map { cartProduct ->
                                cartProduct.copy(isChecked = true)
                            }

//                        val orderCartList =
//                            carts.content.map { it.toCartProduct(isChecked = true) }
//                                .associateBy { cartProduct -> cartProduct.cart.id }.toMutableMap()

                        _uiState.postValue(
                            state.copy(
                                orderCarts = carts.content,
                                pagingCartProduct = state.pagingCartProduct.copy(cartList = newPagingCartProduct),
                            ),
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
