package woowacourse.shopping.ui.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import woowacourse.shopping.data.remote.retrofit.dto.OrderInfo
import woowacourse.shopping.data.remote.retrofit.toApiFailure
import woowacourse.shopping.data.remote.retrofit.toUserMessage
import woowacourse.shopping.domain.repository.OrderRepository

class OrderViewModel(
    private val orderRepository: OrderRepository,
) : ViewModel() {
    private val _event = MutableSharedFlow<OrderEvent>(extraBufferCapacity = 1)
    val event: SharedFlow<OrderEvent> = _event.asSharedFlow()

    fun order(
        orderInfo: OrderInfo,
        onSuccess: (() -> Unit)? = null,
    ) {
        viewModelScope.launch {
            runCatching {
                orderRepository
                    .order(
                        orderInfo = orderInfo,
                    )
            }.onSuccess {
                _event.emit(OrderEvent.Success)
                onSuccess?.invoke()
            }.onFailure { throwable ->
                _event.emit(
                    OrderEvent.Failure(
                        throwable
                            .toApiFailure()
                            .toUserMessage(defaultMessage = "주문 처리에 실패했습니다."),
                    ),
                )
            }
        }
    }

    sealed interface OrderEvent {
        data object Success : OrderEvent

        data class Failure(
            val message: String,
        ) : OrderEvent
    }
}
