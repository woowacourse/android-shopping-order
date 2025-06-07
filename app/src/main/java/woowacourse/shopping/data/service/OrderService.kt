package woowacourse.shopping.data.service

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import woowacourse.shopping.data.model.order.OrderRequest

interface OrderService {
    @POST("/orders")
    suspend fun postOrder(
        @Header("Authorization") key: String,
        @Body orderRequest: OrderRequest,
    ): Response<Unit>
}
