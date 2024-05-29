package woowacourse.shopping.data.remote.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface OrderApiService {
    @POST("/orders")
    fun orderItems(
        @Header("accept") accept: String = "*/*",
        @Body cartItemIds : List<Int>,
    ): Call<Unit>
}
