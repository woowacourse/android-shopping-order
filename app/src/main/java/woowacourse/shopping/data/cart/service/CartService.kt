package woowacourse.shopping.data.cart.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.cart.dto.CartItemRequest
import woowacourse.shopping.data.cart.dto.CartQuantityResponse
import woowacourse.shopping.data.cart.dto.CartResponse
import woowacourse.shopping.data.product.dto.CartRequest

interface CartService {
    @GET("/cart-items")
    fun getCart(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<CartResponse>

    @GET("/cart-items")
    fun getAllCart(): Call<CartResponse>

    @POST("/cart-items")
    fun postCartItem(
        @Body request: CartRequest,
    ): Call<Unit>

    @DELETE("/cart-items/{cartItemId}")
    fun deleteShoppingCartItem(
        @Path("cartItemId") cartItemId: Long,
    ): Call<Unit>

    @PATCH("/cart-items/{id}")
    fun patchCartItemQuantity(
        @Path("id") id: Long,
        @Body request: CartItemRequest,
    ): Call<Unit>

    @GET("/cart-items/counts")
    fun getCartItemQuantity(): Call<CartQuantityResponse>
}
