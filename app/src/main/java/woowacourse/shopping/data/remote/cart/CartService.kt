package woowacourse.shopping.data.remote.cart

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface CartService {
    @GET("/cart-items")
    fun requestCart(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 30
    ): Call<CartResponse>

    @POST("/cart-items")
    fun addToCart(
        @Header("accept") accept: String = "*/*",
        @Body cartRequest: CartRequest
    ): Call<Void>
}