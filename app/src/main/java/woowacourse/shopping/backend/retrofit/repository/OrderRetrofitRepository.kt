package woowacourse.shopping.backend.retrofit.repository

import woowacourse.shopping.backend.retrofit.api.OrderRetrofit
import woowacourse.shopping.backend.retrofit.dto.OrderInfo
import woowacourse.shopping.backend.retrofit.throwOnFailure

class OrderRetrofitRepository(
    private val apiService: OrderRetrofit,
) {
    suspend fun order(order: OrderInfo) {
        apiService
            .order(
                order = order,
            ).throwOnFailure(errorPrefix = "주문 실패")
    }
}
