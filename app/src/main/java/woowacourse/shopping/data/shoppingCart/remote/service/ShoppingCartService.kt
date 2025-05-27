package woowacourse.shopping.data.shoppingCart.remote.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import woowacourse.shopping.data.shoppingCart.remote.dto.CartCountsDto
import woowacourse.shopping.data.shoppingCart.remote.dto.CartItemRequestDto

interface ShoppingCartService {
    @GET("cart-items/counts")
    fun getCartCounts(): Call<CartCountsDto>

    @POST("cart-items")
    fun postCartItem(
        @Body cartItemRequestDto: CartItemRequestDto,
    ): Call<Unit>
}
