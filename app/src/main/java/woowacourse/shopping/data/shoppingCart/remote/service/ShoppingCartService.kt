package woowacourse.shopping.data.shoppingCart.remote.service

import retrofit2.Call
import retrofit2.http.GET
import woowacourse.shopping.data.shoppingCart.remote.dto.CartCountsDto

interface ShoppingCartService {
    @GET("cart-items/counts")
    fun getCartCounts(): Call<CartCountsDto>
}
