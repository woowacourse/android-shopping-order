package woowacourse.shopping.data.source

import retrofit2.Call
import woowacourse.shopping.data.remote.dto.cart.CartItemQuantityDto
import woowacourse.shopping.data.remote.dto.cart.CartItemResponse

interface CartItemDataSource {
    fun loadCartItems(): Call<CartItemResponse>

    fun loadCartItems(
        page: Int,
        size: Int,
    ): Call<CartItemResponse>

    fun addCartItem(
        productId: Int,
        quantity: Int,
    ): Call<Unit>

    fun deleteCartItem(id: Int): Call<Unit>

    fun updateCartItem(
        id: Int,
        quantity: Int,
    ): Call<Unit>

    fun loadCartItemCount(): Call<CartItemQuantityDto>
}
