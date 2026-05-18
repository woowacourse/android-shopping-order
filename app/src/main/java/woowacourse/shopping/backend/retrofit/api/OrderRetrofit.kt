package woowacourse.shopping.backend.retrofit.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import woowacourse.shopping.backend.retrofit.dto.OrderInfo

interface OrderRetrofit {
    @POST("/orders")
    fun order(
        @Header("Accept") accept: String = "*/*",
        @Body order: OrderInfo,
    ): Call<Void>
}
