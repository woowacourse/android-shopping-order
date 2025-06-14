package woowacourse.shopping.data.network.service

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.data.network.request.OrderRequest

interface OrderService {
    @POST("/orders")
    suspend fun createOrder(
        @Body request: OrderRequest,
    ): Response<Unit>
}
