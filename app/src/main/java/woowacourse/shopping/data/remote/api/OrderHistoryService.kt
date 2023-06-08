package woowacourse.shopping.data.remote.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import woowacourse.shopping.data.remote.response.OrderCompleteResponseDto

interface OrderHistoryService {

    @GET("orders")
    fun getOrderHistory(
        @Header("Authorization") token: String,
    ): Call<List<OrderCompleteResponseDto>>
}
