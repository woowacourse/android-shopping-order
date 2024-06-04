package woowacourse.shopping.data.remote

import retrofit2.Call
import woowacourse.shopping.data.datasource.CartDataSource
import woowacourse.shopping.data.model.CartItemRequestBody
import woowacourse.shopping.data.model.CartQuantity
import woowacourse.shopping.data.model.CartResponse

class RemoteCartDataSource(
    private val cartService: CartService,
) : CartDataSource {
    override fun getCartItems(
        page: Int,
        size: Int,
        sort: String,
    ): Call<CartResponse> {
        return cartService.getCartItems(page, size, sort)
    }

    override fun addCartItem(cartItemRequestBody: CartItemRequestBody): Call<Unit> {
        return cartService.addCartItem(cartItemRequestBody)
    }

    override fun deleteCartItem(productId: Int): Call<Unit> {
        return cartService.deleteCartItem(productId)
    }

    override fun updateCartItem(
        productId: Int,
        cartQuantity: CartQuantity,
    ): Call<Unit> {
        return cartService.updateCartItem(productId, cartQuantity)
    }

    override fun getCartTotalQuantity(): Call<CartQuantity> {
        return cartService.getCartTotalQuantity()
    }
}
