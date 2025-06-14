package woowacourse.shopping.data.cart.repository

import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.cart.PagedCartItems

interface CartRepository {
    suspend fun loadPagedCartItems(
        page: Int,
        size: Int,
    ): PagedCartItems

    suspend fun loadCart(): List<CartItem>

    suspend fun addCartItem(
        productId: Long,
        quantity: Int,
    )

    suspend fun remove(cartItemId: Long)

    suspend fun updateCartItemQuantity(
        cartItemId: Long,
        quantity: Int,
    )

    suspend fun cartItemsSize(): Int
}
