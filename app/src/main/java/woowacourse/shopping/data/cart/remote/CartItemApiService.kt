package woowacourse.shopping.data.cart.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.cart.remote.dto.CartItemRequest
import woowacourse.shopping.data.cart.remote.dto.CartItemResponse

interface CartItemApiService {
    @GET("/cart-items")
    suspend fun requestCartItems(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = MAX_CART_ITEMS,
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

    companion object {
        const val MAX_CART_ITEMS = 50
    }
}
