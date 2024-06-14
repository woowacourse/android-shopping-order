package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.cart.CartItemRequestBody
import woowacourse.shopping.data.model.cart.CartQuantity
import woowacourse.shopping.data.model.cart.CartResponse
import woowacourse.shopping.data.remote.CartService

class DefaultRemoteCartDataSource(
    private val cartService: CartService,
) : RemoteCartDataSource {
    override suspend fun getCartItems(
        page: Int,
        size: Int,
        sort: String,
    ): CartResponse {
        return cartService.getCartItems(page, size, sort)
    }

    override suspend fun addCartItem(cartItemRequestBody: CartItemRequestBody) {
        return cartService.addCartItem(cartItemRequestBody)
    }

    override suspend fun deleteCartItem(cartItemId: Int) {
        return cartService.deleteCartItem(cartItemId)
    }

    override suspend fun updateCartItem(
        cartItemId: Int,
        cartQuantity: CartQuantity,
    ) {
        return cartService.updateCartItem(cartItemId, cartQuantity)
    }

    override suspend fun getCartTotalQuantity(): CartQuantity {
        return cartService.getCartTotalQuantity()
    }
}
