package woowacourse.shopping.domain.repository

import woowacourse.shopping.ui.model.CartItem
import woowacourse.shopping.ui.model.CartItem2

interface ShoppingCartRepository {
    fun loadAllCartItems(): List<CartItem>

    fun loadAllCartItems2(): List<CartItem2>

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
