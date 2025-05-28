package woowacourse.shopping.data.cart.repository

import woowacourse.shopping.domain.cart.PageableCartItems
import woowacourse.shopping.domain.product.CartItem

interface CartRepository {
    fun loadPageableCartItems(
        page: Int,
        size: Int,
        onLoad: (Result<PageableCartItems>) -> Unit,
    )

    fun loadCart(onLoad: (Result<List<CartItem>>) -> Unit)

    fun addCartItem(
        productId: Long,
        quantity: Int,
        onAdd: (Result<Unit>) -> Unit,
    )

    fun remove(
        cartItemId: Long,
        onRemove: (Result<Unit>) -> Unit,
    )

    fun updateCartItemQuantity(
        cartItemId: Long,
        quantity: Int,
        onUpdate: (Result<Unit>) -> Unit,
    )

    fun cartItemsSize(onResult: (Result<Int>) -> Unit)
}
