package woowacourse.shopping.presentation.ui.shoppingcart.orderrecommend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductHistoryRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.presentation.base.BaseViewModel
import woowacourse.shopping.presentation.base.BaseViewModelFactory
import woowacourse.shopping.presentation.base.Event
import woowacourse.shopping.presentation.base.emit
import woowacourse.shopping.presentation.common.ProductCountHandler

class OrderRecommendViewModel(
    private val productHistoryRepository: ProductHistoryRepository,
    private val shoppingCartRepository: ShoppingCartRepository,
    private val orderRepository: OrderRepository,
) : BaseViewModel(), ProductCountHandler {
    private val _uiState: MutableLiveData<OrderRecommendUiState> =
        MutableLiveData(OrderRecommendUiState())
    val uiState: LiveData<OrderRecommendUiState> get() = _uiState

    private val _navigateAction: MutableLiveData<Event<OrderRecommendNavigateAction>> =
        MutableLiveData(null)
    val navigateAction: LiveData<Event<OrderRecommendNavigateAction>> get() = _navigateAction

    init {
        recommendProductLoad()
    }

    private fun recommendProductLoad() {
        viewModelScope.launch {
            productHistoryRepository.getProductHistoriesByCategory(10)
                .onSuccess { recommendProducts ->

                    hideError()
                    _uiState.value?.let { state ->
                        _uiState.value = state.copy(recommendCarts = recommendProducts)
                    }
                }.onFailure { e -> showError(e) }
        }
    }

    fun load(orderCarts: List<Cart>) {
        _uiState.value?.let { state ->
            _uiState.value =
                state.copy(orderCarts = orderCarts.associateBy { cart -> cart.id }.toMutableMap())
        }
    }

    override fun retry() {}

    fun order() {
        viewModelScope.launch {
            _uiState.value?.let { state ->
                orderRepository.insertOrderByIds(state.orderCarts.keys.toList())
                    .onSuccess {
                        hideError()
                        _navigateAction.emit(OrderRecommendNavigateAction.NavigateToProductList)
                    }.onFailure { e ->
                        showError(e)
                    }
            }
        }
    }

    override fun plusProductQuantity(
        productId: Long,
        position: Int,
    ) {
        updateProductQuantity(productId, increment = true)
    }

    override fun minusProductQuantity(
        productId: Long,
        position: Int,
    ) {
        updateProductQuantity(productId, increment = false)
    }

    private fun updateProductQuantity(
        productId: Long,
        increment: Boolean,
    ) {
        _uiState.value?.let { state ->
            val updatedRecommendCarts =
                state.recommendCarts.map { cart ->
                    if (cart.product.id == productId) {
                        state.orderCarts[cart.id] = cart
                        cart.updateProduct(increment)
                    } else {
                        cart
                    }
                }

            _uiState.value =
                state.copy(
                    recommendCarts = updatedRecommendCarts,
                    orderCarts = state.orderCarts,
                )
        }
    }

    private fun Cart.updateProduct(increment: Boolean): Cart {
        val updatedQuantity = if (increment) this.quantity + 1 else this.quantity - 1
        when {
            this.quantity == 0 -> insertCartProduct(this.product, updatedQuantity)
            updatedQuantity == 0 -> deleteCartProduct(this.id)
            else -> updateCartProduct(this, updatedQuantity)
        }
        return this.copy(quantity = updatedQuantity)
    }

    private fun insertCartProduct(
        product: Product,
        quantity: Int,
    ) {
        viewModelScope.launch {
            shoppingCartRepository.insertCartProduct(
                productId = product.id,
                quantity = quantity,
            ).onSuccess { cartId ->
                hideError()
                _uiState.value?.let { state ->
                    val updateRecommendCarts =
                        state.recommendCarts.map { cart ->
                            if (cart.product.id == product.id) {
                                state.orderCarts[cartId] = cart.copy(id = cartId, quantity = 1)
                                cart.copy(id = cartId)
                            } else {
                                cart
                            }
                        }

                    _uiState.value =
                        state.copy(
                            recommendCarts = updateRecommendCarts,
                            orderCarts = state.orderCarts,
                        )
                }
            }.onFailure { e ->
                showError(e)
            }
        }
    }

    private fun deleteCartProduct(cartId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            shoppingCartRepository.deleteCartProductById(
                cartId = cartId,
            ).onSuccess {
                _uiState.value?.let { state ->
                    state.orderCarts.remove(cartId)
                    _uiState.value =
                        state.copy(
                            orderCarts = state.orderCarts,
                        )
                }
            }.onFailure { e ->
                showError(e)
            }
        }
    }

    private fun updateCartProduct(
        cart: Cart,
        quantity: Int,
    ) {
        viewModelScope.launch {
            shoppingCartRepository.updateCartProduct(
                cartId = cart.id,
                quantity = quantity,
            ).onSuccess {
                _uiState.value?.let { state ->
                    state.orderCarts[cart.id] = cart.copy(quantity = quantity)
                    _uiState.value = state.copy(orderCarts = state.orderCarts)
                }
            }.onFailure { e ->
                showError(e)
            }
        }
    }

    companion object {
        fun factory(
            productHistoryRepository: ProductHistoryRepository,
            shoppingCartRepository: ShoppingCartRepository,
            orderRepository: OrderRepository,
        ): ViewModelProvider.Factory {
            return BaseViewModelFactory {
                OrderRecommendViewModel(
                    productHistoryRepository,
                    shoppingCartRepository,
                    orderRepository,
                )
            }
        }
    }
}
