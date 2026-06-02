package woowacourse.shopping.data.remote.server.service

import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.data.remote.server.dto.order.PostOrderRequest

interface OrderService {
    @POST("orders")
    suspend fun postOrder(
        @Body request: PostOrderRequest,
    )
}
