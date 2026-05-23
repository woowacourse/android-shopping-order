package woowacourse.shopping.data.repository

import woowacourse.shopping.model.cart.Cart
import woowacourse.shopping.model.cart.CartItem
import woowacourse.shopping.model.Page

interface CartRepository {
    suspend fun getAllCartItems(): Cart

    suspend fun add(
        productId: Long,
        quantity: Int,
    ): Long

    suspend fun updateQuantity(
        cartItemId: Long,
        quantity: Int,
    )

    suspend fun delete(cartItemId: Long)

    suspend fun getPagedItems(
        page: Int,
        size: Int,
    ): Page<CartItem>

    suspend fun getTotalProductCount(): Int
}
