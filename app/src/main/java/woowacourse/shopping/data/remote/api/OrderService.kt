package woowacourse.shopping.data.remote.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import woowacourse.shopping.data.remote.response.OrderResponseDto

interface OrderService {

    @GET("/coupons")
    fun getCoupons(
        @Header("Authorization") token: String,
    ): Call<List<OrderResponseDto>>
}
