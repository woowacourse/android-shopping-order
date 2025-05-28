package woowacourse.shopping.data.shoppingCart.repository

import woowacourse.shopping.domain.cart.PageableCartItems
import woowacourse.shopping.domain.product.CartItem
import woowacourse.shopping.domain.product.Product

interface ShoppingCartRepository {
    fun load(
        page: Int,
        size: Int,
        onLoad: (Result<PageableCartItems>) -> Unit,
    )

    fun upsert(
        cartItem: CartItem,
        onAdd: (Result<Unit>) -> Unit,
    )

    fun remove(
        cartItem: CartItem,
        onRemove: (Result<Unit>) -> Unit,
    )

    fun update(
        cartItems: List<CartItem>,
        onUpdate: (Result<Unit>) -> Unit,
    )

    fun quantityOf(
        product: Product,
        onResult: (Result<Int>) -> Unit,
    )
}
