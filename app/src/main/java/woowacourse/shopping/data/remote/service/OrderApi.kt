package woowacourse.shopping.data.remote.service

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import woowacourse.shopping.data.remote.dto.request.OrderRequest

interface OrderApi {
    @POST("orders")
    suspend fun submitOrders(
        @Header("accept") accept: String = "*/*",
        @Body orderRequest: OrderRequest,
    )
}
