package woowacourse.shopping.backend.retrofit.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import woowacourse.shopping.backend.retrofit.dto.OrderInfo

interface OrderRetrofit {
    @POST("/orders")
    suspend fun order(
        @Header("Accept") accept: String = "*/*",
        @Body order: OrderInfo,
    ): Response<Unit>
}
