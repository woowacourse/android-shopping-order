package woowacourse.shopping.data.remote.service

import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.data.remote.dto.OrderRequest

interface OrderService {
    @POST("orders")
    suspend fun requestOrder(
        @Body request: OrderRequest,
    )
}
