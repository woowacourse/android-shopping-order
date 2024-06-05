package woowacourse.shopping.remote.order

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface OrderApiService {
    @POST("/orders")
    fun createOrder(
        @Body cartItemIds: OrderRequest,
    ): Call<Unit>
}
