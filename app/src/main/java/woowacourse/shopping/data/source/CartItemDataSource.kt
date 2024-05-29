package woowacourse.shopping.data.source

import retrofit2.Call
import woowacourse.shopping.data.remote.dto.cart.CartItemQuantityDto
import woowacourse.shopping.data.remote.dto.cart.CartItemResponse
import woowacourse.shopping.data.remote.dto.product.ProductResponse

interface CartItemDataSource {
    fun loadCartItems(
        page: Int,
        size: Int,
    ): Call<CartItemResponse>

    fun addCartItem(
        productId: Int,
        quantity: Int,
    ): Call<Unit>

    fun deleteCartItem(
        id : Int
    ): Call<Unit>

    fun updateCartItem(
        id :Int,
        quantity: Int,
    ): Call<Unit>

    fun loadCartItemCount(): Call<CartItemQuantityDto>
}
