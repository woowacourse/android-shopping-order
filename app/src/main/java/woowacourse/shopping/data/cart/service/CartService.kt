package woowacourse.shopping.data.cart.service

import retrofit2.Response
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
    suspend fun getCart(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): CartResponse

    @GET("/cart-items")
    suspend fun getAllCart(): CartResponse

    @POST("/cart-items")
    suspend fun postCartItem(
        @Body request: CartRequest,
    ): Response<Unit>

    @DELETE("/cart-items/{cartItemId}")
    suspend fun deleteShoppingCartItem(
        @Path("cartItemId") cartItemId: Long,
    )

    @PATCH("/cart-items/{id}")
    suspend fun patchCartItemQuantity(
        @Path("id") id: Long,
        @Body request: CartItemRequest,
    )

    @GET("/cart-items/counts")
    suspend fun getCartItemQuantity(): CartQuantityResponse
}
