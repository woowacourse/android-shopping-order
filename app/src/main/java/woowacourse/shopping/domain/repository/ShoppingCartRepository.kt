package woowacourse.shopping.domain.repository

import woowacourse.shopping.ui.model.CartItem

interface ShoppingCartRepository {
    fun loadAllCartItems(): List<CartItem>

    fun shoppingCartProductQuantity(): Int

    fun updateProductQuantity(
        cartItemId: Long,
        quantity: Int,
    )

    fun addShoppingCartProduct(
        productId: Long,
        quantity: Int,
    )

    fun removeShoppingCartProduct(cartItemId: Long)
}
