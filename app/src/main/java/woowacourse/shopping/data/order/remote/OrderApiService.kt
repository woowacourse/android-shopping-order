package woowacourse.shopping.data.order.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.data.order.remote.dto.OrderRequest

interface OrderApiService {
    @POST("/orders")
    suspend fun createOrder(
        @Body cartItemIds: OrderRequest,
    ): Response<Unit>
}
