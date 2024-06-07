package woowacourse.shopping.domain.repository.cart

import woowacourse.shopping.ui.model.CartItem

interface CartItemRepository {
    suspend fun loadCartItems(): List<CartItem>

    suspend fun updateProductQuantity(
        productId: Long,
        quantity: Int,
    )

    suspend fun delete(id: Long)

    suspend fun updateCartItemQuantity(
        cartItemId: Long,
        quantity: Int,
    )

    suspend fun calculateCartItemsCount(): Int
}
