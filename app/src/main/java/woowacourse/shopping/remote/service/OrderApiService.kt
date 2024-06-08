package woowacourse.shopping.remote.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.remote.model.request.OrderRequest

interface OrderApiService {
    @POST("/orders")
    fun createOrder(
        @Body cartItemIds: OrderRequest,
    ): Call<Unit>
}
