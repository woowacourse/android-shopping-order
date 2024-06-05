package woowacourse.shopping.domain.repository.cart

import woowacourse.shopping.ui.model.CartItem

interface CartItemRepository {
    fun loadCartItems(): List<CartItem>

    fun addCartItem(
        id: Long,
        quantity: Int,
    )

    fun removeCartItem(id: Long)

    fun increaseCartProduct(
        id: Long,
        quantity: Int,
    )

    fun decreaseCartProduct(
        id: Long,
        quantity: Int,
    )

    fun increaseCartItem(
        cartItemId: Long,
        quantity: Int,
    )
}
