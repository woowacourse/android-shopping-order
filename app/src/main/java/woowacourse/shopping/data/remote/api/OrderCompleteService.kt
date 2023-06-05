package woowacourse.shopping.data.remote.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import woowacourse.shopping.data.remote.response.OrderCompleteResponseDto

interface OrderCompleteService {

    @GET("/orders/{orderId}")
    fun getReceipt(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: Int,
    ): Call<OrderCompleteResponseDto>
}
