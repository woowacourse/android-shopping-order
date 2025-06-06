package woowacourse.shopping.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.dto.cart.CartItemCountResponse
import woowacourse.shopping.data.dto.cart.CartItemRequest
import woowacourse.shopping.data.dto.cart.CartsResponse
import woowacourse.shopping.data.dto.cart.UpdateCartRequest

interface CartItemService {
    @GET("/cart-items")
    suspend fun requestCartItems(
        @Query("page") page: Int = 0,
        @Query("size") size: Int?,
    ): Response<CartsResponse>

    @POST("/cart-items")
    suspend fun addCartItem(
        @Body request: CartItemRequest,
    ): Response<Unit>

    @PATCH("/cart-items/{id}")
    suspend fun updateCartItem(
        @Path("id") cartId: Long,
        @Body updateCartRequest: UpdateCartRequest,
    ): Response<Unit>

    @DELETE("/cart-items/{id}")
    suspend fun deleteCartItem(
        @Path("id") cartId: Long,
    ): Response<Unit>

    @GET("/cart-items/counts")
    suspend fun requestCartItemCount(): Response<CartItemCountResponse>
}
