package woowacourse.shopping.remote.cart

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CartItemApiService {
    @GET("/cart-items")
    fun requestCartItems(
        @Query("page") page: Int = 0,
        @Query("size") size: Int? = null,
    ): Call<CartItemResponse>

    @POST("/cart-items")
    fun addCartItem(
        @Body cartItemRequest: CartItemRequest,
    ): Call<Unit>

    @PATCH("/cart-items/{id}")
    fun updateCartItemQuantity(
        @Path("id") id: Long,
        @Body quantity: Int,
    ): Call<Unit>

    @DELETE("/cart-items/{id}")
    fun removeCartItem(
        @Path("id") id: Long,
    ): Call<Unit>
}
