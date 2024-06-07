package woowacourse.shopping.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.remote.dto.request.OrderRequest

interface OrderApiService {
    @POST("/orders")
    suspend fun requestOrder(
        @Body orderRequest: OrderRequest,
    ): Response<Unit>
}
