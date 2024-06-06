package woowacourse.shopping.presentation.cart.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import timber.log.Timber
import woowacourse.shopping.domain.usecase.DecreaseCartProductUseCase
import woowacourse.shopping.domain.usecase.IncreaseCartProductUseCase
import woowacourse.shopping.domain.usecase.OrderCartProductsUseCase
import woowacourse.shopping.domain.usecase.RecommendProductsUseCase
import woowacourse.shopping.presentation.base.BaseViewModelFactory
import woowacourse.shopping.presentation.cart.CartItemListener
import woowacourse.shopping.presentation.cart.CartProductUi
import woowacourse.shopping.presentation.shopping.toCartUiModel
import woowacourse.shopping.presentation.util.MutableSingleLiveData
import woowacourse.shopping.presentation.util.SingleLiveData

class OrderViewModel(
    orders: List<CartProductUi>,
    private val orderCartProductsUseCase: OrderCartProductsUseCase,
    private val decreaseCartProductUseCase: DecreaseCartProductUseCase,
    private val increaseCartProductCountUseCase: IncreaseCartProductUseCase,
    recommendProductsUseCase: RecommendProductsUseCase,
) : ViewModel(), CartItemListener {
    private val _uiState = MutableLiveData(OrderUiState(orders))
    val uiState: LiveData<OrderUiState> get() = _uiState
    private val _updateCartEvent = MutableSingleLiveData<Unit>()
    val updateCartEvent: SingleLiveData<Unit> get() = _updateCartEvent
    private val _showOrderDialogEvent = MutableSingleLiveData<Unit>()
    val showOrderDialogEvent: SingleLiveData<Unit> get() = _showOrderDialogEvent
    private val _finishOrderEvent = MutableSingleLiveData<Unit>()
    val finishOrderEvent: SingleLiveData<Unit> get() = _finishOrderEvent
    private val _errorEvent = MutableSingleLiveData<OrderErrorEvent>()
    val errorEvent: SingleLiveData<OrderErrorEvent> get() = _errorEvent

    init {
        viewModelScope.launch {
            val recommendProducts =
                recommendProductsUseCase().map { it.toCartUiModel(initCount = 0) }
            val uiState = _uiState.value
            if (uiState != null) {
                _uiState.value = uiState.copy(recommendProducts = recommendProducts)
            } else {
                _uiState.value = OrderUiState(orders, recommendProducts)
            }
        }
    }

    fun startOrder() {
        _showOrderDialogEvent.setValue(Unit)
    }

    fun orderProducts() {
        viewModelScope.launch {
            val uiState = _uiState.value ?: return@launch
            orderCartProductsUseCase(uiState.totalOrderIds)
                .onSuccess {
                    _updateCartEvent.setValue(Unit)
                    _finishOrderEvent.setValue(Unit)
                }.onFailure {
                    Timber.e(it)
                    _errorEvent.setValue(OrderErrorEvent.OrderProducts)
                }
        }
    }

    override fun increaseProductCount(id: Long) {
        viewModelScope.launch {
            val uiState = _uiState.value ?: return@launch
            increaseCartProductCountUseCase(id, INCREMENT_AMOUNT)
                .onSuccess {
                    _uiState.value = uiState.increaseProductCount(id, INCREMENT_AMOUNT)
                    _updateCartEvent.setValue(Unit)
                }.onFailure {
                    Timber.e(it)
                    _errorEvent.setValue(OrderErrorEvent.IncreaseCartProduct)
                }
        }
    }

    override fun decreaseProductCount(id: Long) {
        viewModelScope.launch {
            val uiState = _uiState.value ?: return@launch
            decreaseCartProductUseCase(id).onSuccess {
                _uiState.value = uiState.decreaseProductCount(
                    id,
                    INCREMENT_AMOUNT,
                )
                _updateCartEvent.setValue(Unit)
            }.onFailure {
                Timber.e(it)
                _errorEvent.setValue(OrderErrorEvent.DecreaseCartProduct)
            }
        }
    }

    companion object {
        private const val INCREMENT_AMOUNT = 1

        fun factory(
            orders: List<CartProductUi>,
            orderCartProductsUseCase: OrderCartProductsUseCase,
            decreaseCartProductUseCase: DecreaseCartProductUseCase,
            increaseCartProductCountUseCase: IncreaseCartProductUseCase,
            recommendProductsUseCase: RecommendProductsUseCase,
        ): ViewModelProvider.Factory {
            return BaseViewModelFactory {
                OrderViewModel(
                    orders,
                    orderCartProductsUseCase,
                    decreaseCartProductUseCase,
                    increaseCartProductCountUseCase,
                    recommendProductsUseCase,
                )
            }
        }
    }
}

private fun OrderUiState.increaseProductCount(
    productId: Long,
    amount: Int,
): OrderUiState =
    copy(
        recommendProducts =
        recommendProducts.map {
            if (it.product.id == productId) {
                it.copy(count = it.count + amount)
            } else {
                it
            }
        },
    )

private fun OrderUiState.decreaseProductCount(
    productId: Long,
    amount: Int,
): OrderUiState {
    val newProducts =
        recommendProducts.map {
            if (it.product.id == productId) {
                it.copy(count = it.count - amount)
            } else {
                it
            }
        }
    return copy(recommendProducts = newProducts)
}
