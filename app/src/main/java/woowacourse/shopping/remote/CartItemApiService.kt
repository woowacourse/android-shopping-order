package woowacourse.shopping.remote

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CartItemApiService {
    @GET("/cart-items")
    fun requestCartItems(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 5,
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
}
