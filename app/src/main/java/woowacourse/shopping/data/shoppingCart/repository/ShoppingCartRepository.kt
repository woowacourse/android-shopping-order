package woowacourse.shopping.data.shoppingCart.repository

import woowacourse.shopping.domain.cart.PageableCartItems
import woowacourse.shopping.domain.product.CartItem

interface ShoppingCartRepository {
    fun load(
        page: Int,
        size: Int,
        onLoad: (Result<PageableCartItems>) -> Unit,
    )

    fun addCartItem(
        productId: Long,
        quantity: Int,
        onAdd: (Result<Unit>) -> Unit,
    )

    fun remove(
        cartItem: CartItem,
        onRemove: (Result<Unit>) -> Unit,
    )

    fun updateCartItemQuantity(
        cartItemId: Long,
        quantity: Int,
        onUpdate: (Result<Unit>) -> Unit,
    )

    fun quantity(onResult: (Result<Int>) -> Unit)
}
