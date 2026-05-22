package woowacourse.shopping.ui.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.R
import woowacourse.shopping.network.NetworkMonitor
import woowacourse.shopping.repository.CartRepository
import woowacourse.shopping.repository.ShoppingRepositoryProvider
import woowacourse.shopping.ui.cart.SelectedCartOrder

private const val DEFAULT_DELIVERY_FEE = 3_000L

class OrderViewModel(
    private val cartRepository: CartRepository = ShoppingRepositoryProvider.cartRepository,
    private val networkMonitor: NetworkMonitor = ShoppingRepositoryProvider.networkMonitor,
) : ViewModel() {
    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()

    private val _events =
        MutableSharedFlow<OrderEvent>(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )
    val events: SharedFlow<OrderEvent> = _events

    private var pendingOrder: SelectedCartOrder? = null

    init {
        observeNetworkState()
    }

    fun startOrder(selectedCartOrder: SelectedCartOrder) {
        pendingOrder = selectedCartOrder

        _uiState.update { currentState ->
            currentState.copy(
                coupons = emptyList(),
                priceSummary = selectedCartOrder.toPriceSummary(),
                isOrdering = false,
            )
        }
    }

    fun placeOrder() {
        val targetOrder = pendingOrder ?: return
        if (targetOrder.items.isEmpty() || _uiState.value.isOrdering) return

        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(isOrdering = true)
            }

            runCatching {
                cartRepository.createOrder(targetOrder.items.map { it.cartItemId })
            }.onSuccess {
                pendingOrder = null
                _uiState.update { currentState ->
                    currentState.copy(
                        coupons = emptyList(),
                        priceSummary = emptyPriceSummary(),
                        isOrdering = false,
                    )
                }
                _events.emit(OrderEvent.OrderCompleted)
            }.onFailure { throwable ->
                _uiState.update { currentState ->
                    currentState.copy(isOrdering = false)
                }
                _events.emit(OrderEvent.ShowMessage(throwable.message ?: "주문에 실패했습니다."))
            }
        }
    }

    private fun observeNetworkState() {
        viewModelScope.launch {
            networkMonitor.isNetworkConnected.collect { isConnected ->
                _uiState.update { currentState ->
                    currentState.copy(isNetworkConnected = isConnected)
                }
            }
        }
    }

    private fun SelectedCartOrder.toPriceSummary(): OrderPriceSummaryUiModel {
        val orderAmount = items.sumOf { it.price.toLong() * it.quantity }
        val deliveryFee = if (orderAmount > 0) DEFAULT_DELIVERY_FEE else 0

        return OrderPriceSummaryUiModel(
            items =
                listOf(
                    OrderPriceLineUiModel(
                        labelResId = R.string.order_price_label_order_amount,
                        price = orderAmount,
                    ),
                    OrderPriceLineUiModel(
                        labelResId = R.string.order_price_label_coupon_discount,
                        price = 0,
                    ),
                    OrderPriceLineUiModel(
                        labelResId = R.string.order_price_label_delivery_fee,
                        price = deliveryFee,
                    ),
                ),
            totalPaymentPrice = orderAmount + deliveryFee,
        )
    }
}
