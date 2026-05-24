package woowacourse.shopping.ui.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.remote.retrofit.dto.OrderInfo
import woowacourse.shopping.domain.repository.OrderRepository

class OrderViewModel(
    private val orderRepository: OrderRepository,
) : ViewModel() {
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
                onSuccess?.invoke()
            }
        }
    }
}
