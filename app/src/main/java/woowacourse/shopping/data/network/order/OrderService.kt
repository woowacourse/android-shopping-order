package woowacourse.shopping.data.network.order

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import woowacourse.shopping.data.network.order.dto.OrderRequestDto

interface OrderService {
    @POST("/orders")
    suspend fun orders(
        @Header("accept")
        accept: String = "*/*",
        @Body
        orderRequest: OrderRequestDto,
    ): Unit
}
