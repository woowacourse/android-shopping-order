package woowacourse.shopping.remote.service

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.remote.dto.CartItemRequest
import woowacourse.shopping.remote.dto.CartQuantityDto
import woowacourse.shopping.remote.dto.CartResponse

interface CartService {
    @GET("/cart-items")
    suspend fun getCartItems(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String,
    ): CartResponse

    @POST("/cart-items")
    suspend fun addCartItem(
        @Body cartItemRequest: CartItemRequest,
    ): Response<Unit>

    @DELETE("/cart-items/{id}")
    suspend fun deleteCartItem(
        @Path("id") id: Int,
    ): Response<Unit>

    @PATCH("/cart-items/{id}")
    suspend fun updateCartItem(
        @Path("id") id: Int,
        @Body cartQuantityDto: CartQuantityDto,
    ): Response<Unit>

    @GET("/cart-items/counts")
    suspend fun getCartTotalQuantity(): Response<CartQuantityDto>
}
