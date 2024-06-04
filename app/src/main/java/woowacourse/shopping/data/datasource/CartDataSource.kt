package woowacourse.shopping.data.datasource

import retrofit2.Call
import woowacourse.shopping.data.dto.CartItemRequest
import woowacourse.shopping.data.dto.CartQuantityDto
import woowacourse.shopping.data.dto.CartResponse

interface CartDataSource {
    fun getCartItems(
        page: Int,
        size: Int,
        sort: String,
    ): Call<CartResponse>

    fun addCartItem(cartItemRequest: CartItemRequest): Call<Unit>

    fun deleteCartItem(productId: Int): Call<Unit>

    fun updateCartItem(
        productId: Int,
        cartQuantityDto: CartQuantityDto,
    ): Call<Unit>

    fun getCartTotalQuantity(): Call<CartQuantityDto>
}
