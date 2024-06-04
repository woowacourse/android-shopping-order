package woowacourse.shopping.data.datasource

import retrofit2.Call
import woowacourse.shopping.data.model.CartItemRequestBody
import woowacourse.shopping.data.model.CartQuantity
import woowacourse.shopping.data.model.CartResponse

interface CartDataSource {
    fun getCartItems(
        page: Int,
        size: Int,
        sort: String,
    ): Call<CartResponse>

    fun addCartItem(cartItemRequestBody: CartItemRequestBody): Call<Unit>

    fun deleteCartItem(productId: Int): Call<Unit>

    fun updateCartItem(
        productId: Int,
        cartQuantity: CartQuantity,
    ): Call<Unit>

    fun getCartTotalQuantity(): Call<CartQuantity>
}
