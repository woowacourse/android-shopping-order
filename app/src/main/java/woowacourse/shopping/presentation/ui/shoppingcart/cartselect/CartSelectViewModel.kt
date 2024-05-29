package woowacourse.shopping.presentation.ui.shoppingcart.cartselect

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.presentation.base.BaseViewModel
import woowacourse.shopping.presentation.base.BaseViewModelFactory
import woowacourse.shopping.presentation.base.MessageProvider
import woowacourse.shopping.presentation.common.ProductCountHandler
import woowacourse.shopping.presentation.ui.shoppingcart.cartselect.adapter.ShoppingCartPagingSource
import kotlin.concurrent.thread

class CartSelectViewModel(
    private val shoppingRepository: ShoppingCartRepository,
    private val orderRepository: OrderRepository,
) :
    BaseViewModel(),
        CartSelectActionHandler,
        ProductCountHandler {
    private val _uiState: MutableLiveData<CartSelectUiState> =
        MutableLiveData(CartSelectUiState())
    val uiState: LiveData<CartSelectUiState> get() = _uiState

    private val shoppingCartPagingSource = ShoppingCartPagingSource(shoppingRepository)

    init {
        loadCartProducts(INIT_PAGE)
    }

    private fun loadCartProducts(page: Int) {
        thread {
            showLoading()
            Thread.sleep(1000) // TODO 스켈레톤 UI를 보여주기 위한 sleep..zzz

            shoppingCartPagingSource.load(page).onSuccess { pagingCartProduct ->
                hideError()

                val newCartList =
                    pagingCartProduct.cartList.map { cart ->
                        if (cart.id in (uiState.value?.cartIdList ?: emptyList())) {
                            cart.copy(isChecked = true)
                        } else {
                            cart
                        }
                    }

                val newPagingCartProduct = pagingCartProduct.copy(cartList = newCartList)

                _uiState.value?.let { state ->
                    _uiState.postValue(state.copy(pagingCartProduct = newPagingCartProduct))
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
                        cart.updateProduct(increment)
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
        thread {
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
        thread {
            shoppingRepository.deleteCartProduct(cartId = cartId).onSuccess {
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

    override fun checkCartProduct(cartId: Int) {
        _uiState.value?.let { state ->
            _uiState.value = state.copy(cartIdList = state.cartIdList + cartId)
        }
    }

    private fun updateCartProduct(
        cartId: Int,
        quantity: Int,
    ) {
        thread {
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

    companion object {
        const val INIT_PAGE = 0

        fun factory(
            shoppingCartRepository: ShoppingCartRepository,
            orderRepository: OrderRepository,
        ): ViewModelProvider.Factory {
            return BaseViewModelFactory {
                CartSelectViewModel(
                    shoppingCartRepository,
                    orderRepository,
                )
            }
        }
    }
}
