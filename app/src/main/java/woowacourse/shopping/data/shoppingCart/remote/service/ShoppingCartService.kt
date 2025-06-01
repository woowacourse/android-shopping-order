package woowacourse.shopping.data.shoppingCart.remote.service

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.shoppingCart.remote.dto.CartCountsResponseDto
import woowacourse.shopping.data.shoppingCart.remote.dto.CartItemQuantityRequestDto
import woowacourse.shopping.data.shoppingCart.remote.dto.CartItemRequestDto
import woowacourse.shopping.data.shoppingCart.remote.dto.ShoppingCartItemsResponseDto

interface ShoppingCartService {
    @GET("cart-items/counts")
    suspend fun getCartCounts(): CartCountsResponseDto

    @POST("cart-items")
    suspend fun postCartItem(
        @Body cartItemRequestDto: CartItemRequestDto,
    )

    @PATCH("cart-items/{id}")
    suspend fun updateCartItemQuantity(
        @Path("id") shoppingCartId: Long,
        @Body cartItemQuantityRequestDto: CartItemQuantityRequestDto,
    )

    @GET("cart-items")
    suspend fun getCartItems(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): ShoppingCartItemsResponseDto

    @DELETE("cart-items/{id}")
    suspend fun deleteCartItem(
        @Path("id") shoppingCartId: Long,
    )
}
