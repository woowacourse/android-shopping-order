package woowacourse.shopping.data.source.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import woowacourse.shopping.data.model.OrderRequest

interface OrderApiService {
    @POST("/orders")
    suspend fun postOrders(
        @Header("accept") accept: String = "*/*",
        @Body request: OrderRequest,
    ): Response<Unit>
}
