package woowacourse.shopping.data.remote.cart

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CartService {
    @GET("/cart-items")
    fun requestCart(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 30,
    ): Call<CartResponse>

    @POST("/cart-items")
    fun addToCart(
        @Header("accept") accept: String = "*/*",
        @Body cartRequest: CartRequest,
    ): Call<Void>

    @DELETE("/cart-items/{id}")
    fun deleteFromCart(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Long,
    ): Call<Void>

    @PATCH("/cart-items/{id}")
    fun updateCart(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Long,
        @Body cartQuantity: CartQuantity,
    ): Call<Void>

    @GET("/cart-items/counts")
    fun getCartCounts(
        @Header("accept") accept: String = "*/*",
    ): Call<CartQuantity>
}
