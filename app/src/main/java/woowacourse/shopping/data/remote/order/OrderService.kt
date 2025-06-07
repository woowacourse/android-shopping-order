package woowacourse.shopping.data.remote.order

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OrderService {
    @POST("/orders")
    suspend fun order(
        @Body orderRequest: OrderRequest,
    ): Response<Unit>
}
