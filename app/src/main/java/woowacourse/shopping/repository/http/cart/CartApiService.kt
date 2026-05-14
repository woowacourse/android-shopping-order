package woowacourse.shopping.repository.http.cart

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CartApiService {
    @GET("cart-items")
    suspend fun getCartItems(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Response<CartPageResponseDto>

    @POST("cart-items")
    suspend fun addCartItem(
        @Body body: CartItemRequestDto,
    ): Response<Unit>

    @PATCH("cart-items/{id}")
    suspend fun updateCartItemQuantity(
        @Path("id") id: Long,
        @Body body: CartItemQuantityUpdateRequestDto,
    ): Response<Unit>

    @DELETE("cart-items/{id}")
    suspend fun deleteCartItem(
        @Path("id") id: Long,
    ): Response<Unit>

    @GET("cart-items/counts")
    suspend fun getCartItemCount(): Response<CartItemCountResponseDto>

    @POST("orders")
    suspend fun createOrder(
        @Body body: OrderRequestDto,
    ): Response<Unit>
}
