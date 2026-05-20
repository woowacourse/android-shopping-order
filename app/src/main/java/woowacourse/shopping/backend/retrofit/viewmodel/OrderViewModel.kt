package woowacourse.shopping.backend.retrofit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import woowacourse.shopping.backend.retrofit.dto.OrderInfo
import woowacourse.shopping.backend.retrofit.repository.OrderRetrofitRepository

class OrderViewModel(
    private val orderRetrofitRepository: OrderRetrofitRepository,
) : ViewModel() {
    private val _event = MutableSharedFlow<OrderEvent>(extraBufferCapacity = 1)
    val event: SharedFlow<OrderEvent> = _event.asSharedFlow()

    fun order(
        orderInfo: OrderInfo,
        onSuccess: (() -> Unit)? = null,
    ) {
        viewModelScope.launch {
            runCatching {
                orderRetrofitRepository
                    .order(
                        order = orderInfo,
                    )
            }.onSuccess {
                _event.tryEmit(OrderEvent.Success)
                onSuccess?.invoke()
            }.onFailure { throwable ->
                _event.tryEmit(OrderEvent.Failure(throwable.message ?: "주문 실패"))
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
