package woowacourse.shopping.remote.cart

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CartItemApiService {
    @GET("/cart-items")
    suspend fun requestCartItems(
        @Query("page") page: Int = 0,
        @Query("size") size: Int? = null,
    ): Response<CartItemResponse>

    @POST("/cart-items")
    suspend fun addCartItem(
        @Body cartItemRequest: CartItemRequest,
    ): Response<Unit>

    @PATCH("/cart-items/{id}")
    suspend fun updateCartItemQuantity(
        @Path("id") id: Long,
        @Body quantity: Int,
    ): Response<Unit>

    @DELETE("/cart-items/{id}")
    suspend fun removeCartItem(
        @Path("id") id: Long,
    ): Response<Unit>
}
