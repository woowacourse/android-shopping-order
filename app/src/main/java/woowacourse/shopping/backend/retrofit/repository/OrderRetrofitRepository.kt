package woowacourse.shopping.backend.retrofit.repository

import retrofit2.Call
import woowacourse.shopping.backend.retrofit.api.OrderRetrofitInterface
import woowacourse.shopping.backend.retrofit.dto.OrderInfo

class OrderRetrofitRepository(
    private val apiService: OrderRetrofitInterface,
) {
    fun order(order: OrderInfo): Call<Void> =
        apiService.order(
            order = order,
        )
}
