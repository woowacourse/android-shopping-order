package woowacourse.shopping.remote.service

import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.remote.dto.OrderRequest

interface OrderService {
    @POST("/orders")
    suspend fun postOrder(
        @Body orderRequest: OrderRequest,
    )
}
