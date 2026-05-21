package woowacourse.shopping.data.network.order

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.data.network.order.dto.OrderRequest

interface OrderService {
    @POST("/orders")
    suspend fun orders(@Body request: OrderRequest): Response<Unit>
}
