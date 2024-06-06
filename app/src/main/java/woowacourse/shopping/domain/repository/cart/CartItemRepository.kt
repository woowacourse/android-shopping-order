package woowacourse.shopping.domain.repository.cart

import woowacourse.shopping.ui.model.CartItem

interface CartItemRepository {
    fun loadCartItems(): List<CartItem>

    fun updateProductQuantity(
        productId: Long,
        quantity: Int,
    )

    fun delete(id: Long)

    fun updateCartItemQuantity(
        cartItemId: Long,
        quantity: Int,
    )

    fun calculateCartItemsCount(): Int
}
