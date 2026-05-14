package woowacourse.shopping.backend.retrofit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.backend.retrofit.awaitCompletion
import woowacourse.shopping.backend.retrofit.dto.OrderInfo
import woowacourse.shopping.backend.retrofit.repository.OrderRetrofitRepository

class OrderViewModel(
    private val orderRetrofitRepository: OrderRetrofitRepository,
) : ViewModel() {
    fun order(orderInfo: OrderInfo) {
        viewModelScope.launch {
            runCatching {
                orderRetrofitRepository
                    .order(
                        order = orderInfo,
                    ).awaitCompletion(errorPrefix = "주문 실패")
            }
        }
    }
}
