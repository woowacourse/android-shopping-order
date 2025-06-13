package woowacourse.shopping.data.service

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.data.dto.order.CreateOrderRequest

interface OrderService {
    @POST("/orders")
    suspend fun createOrder(
        @Body createOrderRequest: CreateOrderRequest,
    ): Response<Unit>
}
