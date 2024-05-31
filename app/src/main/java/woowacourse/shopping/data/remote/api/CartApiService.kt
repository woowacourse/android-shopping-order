package woowacourse.shopping.data.remote.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.remote.dto.cart.CartItemQuantityDto
import woowacourse.shopping.data.remote.dto.cart.CartItemRequest
import woowacourse.shopping.data.remote.dto.cart.CartItemResponse

interface CartApiService {
    @GET("/cart-items")
    fun requestCartItems(
        @Query("page") page: Int = 0,
        @Query("size") size: Int,
    ): Call<CartItemResponse>

    @POST("/cart-items")
    fun insertCartItem(
        @Body cartItemRequest: CartItemRequest,
    ): Call<Unit>

    @DELETE("/cart-items/{id}")
    fun deleteCartItem(
        @Path("id") id: Int,
    ): Call<Unit>

    @PATCH("/cart-items/{id}")
    fun updateCartItem(
        @Path("id") id: Int,
        @Body quantity: CartItemQuantityDto,
    ): Call<Unit>

    @GET("/cart-items/counts")
    fun requestCartItemCount(): Call<CartItemQuantityDto>
}
