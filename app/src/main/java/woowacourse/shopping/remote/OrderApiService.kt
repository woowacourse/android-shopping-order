package woowacourse.shopping.remote

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface OrderApiService {
    @POST("/orders")
    fun createOrder(
        @Body cartItemIds: List<Long> 
    ): Call<Unit>
}