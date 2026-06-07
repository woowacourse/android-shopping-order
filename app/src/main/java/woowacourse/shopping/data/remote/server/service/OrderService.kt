package woowacourse.shopping.data.remote.server.service

import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.data.remote.server.dto.order.OrderRequest

interface OrderService {
    @POST("orders")
    suspend fun requestOrder(
        @Body request: OrderRequest,
    )
}
