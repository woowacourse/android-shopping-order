package woowacourse.shopping.data.order.remote.service

import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.data.order.remote.dto.OrderRequestDto

interface OrderService {
    @POST("orders")
    suspend fun postOrders(
        @Body orderRequestDto: OrderRequestDto,
    )
}
