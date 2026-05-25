package woowacourse.shopping.data.remote.api

import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.data.remote.dto.request.CreateOrderRequestBody

interface OrderApi {
    @POST("/orders")
    suspend fun createOrder(
        @Body createOrderRequestBody: CreateOrderRequestBody,
    )
}
