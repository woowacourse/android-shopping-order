package woowacourse.shopping.data.remote.cart

import retrofit2.Call
import retrofit2.Response
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
    suspend fun requestCart(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 5,
    ): CartResponse

    @POST("/cart-items")
    suspend fun addToCart(
        @Header("accept") accept: String = "*/*",
        @Body cartRequest: CartRequest,
    ): Response<Unit>

    @DELETE("/cart-items/{id}")
    suspend fun deleteFromCart(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Long,
    ): Response<Unit>

    @PATCH("/cart-items/{id}")
    fun updateCart(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Long,
        @Body cartQuantity: CartQuantity,
    ): Call<Unit>

    @GET("/cart-items/counts")
    fun getCartCounts(
        @Header("accept") accept: String = "*/*",
    ): Call<CartQuantity>
}
