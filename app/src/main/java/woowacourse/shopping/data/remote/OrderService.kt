package woowacourse.shopping.data.remote

import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.data.order.remote.CreateOrderRequest

interface OrderService {
    @POST("/orders")
    suspend fun requestCreateOrder(
        @Body createOrderRequest: CreateOrderRequest,
    ): Result<Unit>
}
