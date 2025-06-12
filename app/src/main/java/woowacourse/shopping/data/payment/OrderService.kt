package woowacourse.shopping.data.payment

import retrofit2.http.Body
import retrofit2.http.POST

interface OrderService {
    @POST("/orders")
    suspend fun postOrder(
        @Body request: OrderRequest,
    )
}
