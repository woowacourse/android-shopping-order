package woowacourse.shopping.data.remote.cart

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CartService {
    @GET("/cart-items")
    suspend fun requestCart(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 5,
    ): CartResponse

    @POST("/cart-items")
    suspend fun addToCart(
        @Body cartRequest: CartRequest,
    ): Response<Unit>

    @DELETE("/cart-items/{id}")
    suspend fun deleteFromCart(
        @Path("id") id: Long,
    ): Response<Unit>

    @PATCH("/cart-items/{id}")
    suspend fun updateCart(
        @Path("id") id: Long,
        @Body cartQuantity: CartQuantity,
    ): Response<Unit>

    @GET("/cart-items/counts")
    suspend fun getCartCounts(): Response<CartQuantity>
}
