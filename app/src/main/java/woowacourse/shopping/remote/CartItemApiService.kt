package woowacourse.shopping.remote

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CartItemApiService {
    @GET("/cart-items")
    fun requestCartItems(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 5,
    ): Call<CartItemResponse>
}
