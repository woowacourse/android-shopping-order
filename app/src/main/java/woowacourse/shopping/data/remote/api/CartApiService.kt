package woowacourse.shopping.data.remote.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.remote.dto.cart.CartItemQuantityDto
import woowacourse.shopping.data.remote.dto.cart.CartItemRequest
import woowacourse.shopping.data.remote.dto.cart.CartItemResponse

interface CartApiService {
    @GET("/cart-items")
    suspend fun requestCartItems(
        @Query("page") page: Int = 0,
        @Query("size") size: Int,
    ): CartItemResponse

    @POST("/cart-items")
    suspend fun insertCartItem(
        @Body cartItemRequest: CartItemRequest,
    )

    @DELETE("/cart-items/{id}")
    suspend fun deleteCartItem(
        @Path("id") id: Int,
    )

    @PATCH("/cart-items/{id}")
    suspend fun updateCartItem(
        @Path("id") id: Int,
        @Body quantity: CartItemQuantityDto,
    )

    @GET("/cart-items/counts")
    suspend fun requestCartItemCount(): CartItemQuantityDto
}
