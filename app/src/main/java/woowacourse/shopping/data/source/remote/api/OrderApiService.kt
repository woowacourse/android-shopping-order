package woowacourse.shopping.data.source.remote.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import woowacourse.shopping.data.model.OrderRequest

interface OrderApiService {
    @POST("/orders")
    fun postOrders(
        @Header("accept") accept: String = "*/*",
        @Body request: OrderRequest,
    ): Call<Unit>
}
