package woowacourse.shopping.data.repository.cart

import woowacourse.shopping.domain.CartItem
import woowacourse.shopping.domain.Page

interface CartRepository {
    suspend fun loadCartItemByProductId(id: Long): CartItem?

    suspend fun loadCartItemsByProductIds(ids: List<Long>): List<CartItem>

    suspend fun loadPageOfCartItems(
        pageIndex: Int,
        pageSize: Int,
    ): Page<CartItem>

    suspend fun loadAllCartItems(): List<CartItem>

    suspend fun loadTotalCartCount(): Int

    suspend fun increaseQuantity(cartItem: CartItem)

    suspend fun decreaseQuantity(cartItem: CartItem)

    suspend fun addOrUpdateCartItem(cartItem: CartItem)

    suspend fun addCartItem(cartItem: CartItem): CartItem?

    suspend fun deleteCartItem(cartId: Long)
}
