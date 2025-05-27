package woowacourse.shopping.data.shoppingCart.remote.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.shoppingCart.remote.dto.CartCountsDto
import woowacourse.shopping.data.shoppingCart.remote.dto.CartItemQuantityRequestDto
import woowacourse.shopping.data.shoppingCart.remote.dto.CartItemRequestDto

interface ShoppingCartService {
    @GET("cart-items/counts")
    fun getCartCounts(): Call<CartCountsDto>

    @POST("cart-items")
    fun postCartItem(
        @Body cartItemRequestDto: CartItemRequestDto,
    ): Call<Unit>

    @PATCH("cart-items/{id}")
    fun updateCartItemQuantity(
        @Path("id") productId: Long,
        @Body cartItemQuantityRequestDto: CartItemQuantityRequestDto,
    ): Call<Unit>
}
