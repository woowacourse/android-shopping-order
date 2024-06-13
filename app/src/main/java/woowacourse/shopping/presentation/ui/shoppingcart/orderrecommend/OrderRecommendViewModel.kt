package woowacourse.shopping.presentation.ui.shoppingcart.orderrecommend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Product
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
) : BaseViewModel(), ProductCountHandler {
    private val _recommendCartsUiState: MutableLiveData<RecommendCartsUiState> =
        MutableLiveData(RecommendCartsUiState.Loading)
    val recommendCartsUiState: LiveData<RecommendCartsUiState> get() = _recommendCartsUiState

    private val _orderCartsUiState: MutableLiveData<OrderCartsUiState> =
        MutableLiveData(OrderCartsUiState.Loading)
    val orderCartsUiState: LiveData<OrderCartsUiState> get() = _orderCartsUiState

    private val _navigateAction: MutableLiveData<Event<OrderRecommendNavigateAction>> =
        MutableLiveData(null)
    val navigateAction: LiveData<Event<OrderRecommendNavigateAction>> get() = _navigateAction

    init {
        recommendProductLoad()
    }

    private fun recommendProductLoad() {
        viewModelScope.launch {
            _recommendCartsUiState.value = RecommendCartsUiState.Loading

            productHistoryRepository.getProductHistoriesByCategory(10)
                .onSuccess { recommendProducts ->
                    _recommendCartsUiState.value =
                        RecommendCartsUiState.Success(recommendCarts = recommendProducts)
                }.onFailure { e ->
                    _recommendCartsUiState.value = RecommendCartsUiState.Error(e.message)
                }
        }
    }

    fun load(orderCarts: List<Cart>) {
        _orderCartsUiState.value = OrderCartsUiState.Loading

        _orderCartsUiState.value =
            OrderCartsUiState.Success(
                orderCarts = orderCarts.associateBy { cart -> cart.id }.toMutableMap(),
            )
    }

    override fun retry() {}

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
        (_recommendCartsUiState.value as? RecommendCartsUiState.Success)?.let {
            val updatedRecommendCarts =
                it.recommendCarts.map { cart ->
                    if (cart.product.id == productId) {
                        cart.updateProduct(increment)
                    } else {
                        cart
                    }
                }

            _recommendCartsUiState.value = it.copy(recommendCarts = updatedRecommendCarts)
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
                var targetCart: Cart? = null

                (_recommendCartsUiState.value as? RecommendCartsUiState.Success)?.let {
                    val updateRecommendCarts =
                        it.recommendCarts.map { cart ->
                            if (cart.product.id == product.id) {
                                targetCart = cart
                                cart.copy(id = cartId)
                            } else {
                                cart
                            }
                        }

                    _recommendCartsUiState.value = it.copy(recommendCarts = updateRecommendCarts)
                }

                (_orderCartsUiState.value as? OrderCartsUiState.Success)?.let { state ->
                    targetCart?.let { cart ->
                        state.orderCarts[cartId] = cart.copy(id = cartId, quantity = 1)
                        _orderCartsUiState.value = state.copy(orderCarts = state.orderCarts)
                    }
                }
            }.onFailure { e ->
                _recommendCartsUiState.value = RecommendCartsUiState.Error(e.message)
            }
        }
    }

    private fun deleteCartProduct(cartId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            shoppingCartRepository.deleteCartProductById(
                cartId = cartId,
            ).onSuccess {
                (_orderCartsUiState.value as? OrderCartsUiState.Success)?.let {
                    it.orderCarts.remove(cartId)
                    _orderCartsUiState.value = it.copy(orderCarts = it.orderCarts)
                }
            }.onFailure { e ->
                _orderCartsUiState.value = OrderCartsUiState.Error(e.message)
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
                (_orderCartsUiState.value as? OrderCartsUiState.Success)?.let {
                    it.orderCarts[cart.id] = cart.copy(quantity = quantity)
                    _orderCartsUiState.value = it.copy(orderCarts = it.orderCarts)
                }
            }.onFailure { e ->
                _orderCartsUiState.value = OrderCartsUiState.Error(e.message)
            }
        }
    }

    fun navigateToPayment() {
        (_orderCartsUiState.value as? OrderCartsUiState.Success)?.let { state ->
            _orderCartsUiState.value?.let {
                _navigateAction.emit(
                    OrderRecommendNavigateAction.NavigateToPayment(orderCarts = state.orderCarts.values.toList()),
                )
            }
        }
    }

    companion object {
        fun factory(
            productHistoryRepository: ProductHistoryRepository,
            shoppingCartRepository: ShoppingCartRepository,
        ): ViewModelProvider.Factory {
            return BaseViewModelFactory {
                OrderRecommendViewModel(
                    productHistoryRepository,
                    shoppingCartRepository,
                )
            }
        }
    }
}
