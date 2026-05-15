package woowacourse.shopping.data.remote.retrofit.repository

import retrofit2.Call
import woowacourse.shopping.data.remote.retrofit.api.OrderRetrofitInterface
import woowacourse.shopping.data.remote.retrofit.dto.OrderInfo

class OrderRetrofitRepository(
    private val apiService: OrderRetrofitInterface,
) {
    fun order(order: OrderInfo): Call<Void> =
        apiService.order(
            order = order,
        )
}
