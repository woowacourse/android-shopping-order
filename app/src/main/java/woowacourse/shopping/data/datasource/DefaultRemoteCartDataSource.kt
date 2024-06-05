package woowacourse.shopping.data.datasource

import retrofit2.Call
import woowacourse.shopping.data.model.CartItemRequestBody
import woowacourse.shopping.data.model.CartQuantity
import woowacourse.shopping.data.model.CartResponse
import woowacourse.shopping.data.remote.CartService

class DefaultRemoteCartDataSource(
    private val cartService: CartService,
) : RemoteCartDataSource {
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

    override fun deleteCartItem(cartItemId: Int): Call<Unit> {
        return cartService.deleteCartItem(cartItemId)
    }

    override fun updateCartItem(
        cartItemId: Int,
        cartQuantity: CartQuantity,
    ): Call<Unit> {
        return cartService.updateCartItem(cartItemId, cartQuantity)
    }

    override fun getCartTotalQuantity(): Call<CartQuantity> {
        return cartService.getCartTotalQuantity()
    }
}
