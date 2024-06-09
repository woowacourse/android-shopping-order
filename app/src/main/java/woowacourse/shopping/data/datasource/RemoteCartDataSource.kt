package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.cart.CartItemRequestBody
import woowacourse.shopping.data.model.cart.CartQuantity
import woowacourse.shopping.data.model.cart.CartResponse

interface RemoteCartDataSource {
    suspend fun getCartItems(
        page: Int,
        size: Int,
        sort: String,
    ): CartResponse

    suspend fun addCartItem(cartItemRequestBody: CartItemRequestBody)

    suspend fun deleteCartItem(cartItemId: Int)

    suspend fun updateCartItem(
        cartItemId: Int,
        cartQuantity: CartQuantity,
    )

    suspend fun getCartTotalQuantity(): CartQuantity
}
