package woowacourse.shopping.remote.order

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OrderApiService {
    @POST("/orders")
    suspend fun createOrder(
        @Body cartItemIds: OrderRequest,
    ): Response<Unit>
}
