package woowacourse.shopping.data.service

import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.data.dto.order.Orders

interface OrderService {
    @POST("/orders")
    suspend fun postOrders(
        @Body cartItemIds: Orders,
    )
}
