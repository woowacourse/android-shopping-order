package woowacourse.shopping.data.remote.api

import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.data.remote.dto.request.OrderRequest

interface PaymentApi {
    @POST("/orders")
    suspend fun createOrder(
        @Body orderRequest: OrderRequest,
    )
}
