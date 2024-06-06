package woowacourse.shopping.data.datasource

import retrofit2.Call
import woowacourse.shopping.data.model.CartItemRequestBody
import woowacourse.shopping.data.model.CartQuantity
import woowacourse.shopping.data.model.CartResponse
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
