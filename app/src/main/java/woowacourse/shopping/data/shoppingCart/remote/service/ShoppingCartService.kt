package woowacourse.shopping.data.shoppingCart.remote.service

import retrofit2.Call
import retrofit2.http.Body
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
    fun getCartCounts(): Call<CartCountsResponseDto>

    @POST("cart-items")
    fun postCartItem(
        @Body cartItemRequestDto: CartItemRequestDto,
    ): Call<Unit>

    @PATCH("cart-items/{id}")
    fun updateCartItemQuantity(
        @Path("id") shoppingCartId: Long,
        @Body cartItemQuantityRequestDto: CartItemQuantityRequestDto,
    ): Call<Unit>

    @GET("cart-items")
    fun getCartItems(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<ShoppingCartItemsResponseDto>
}
