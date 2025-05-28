package woowacourse.shopping.data.shoppingCart.storage

import woowacourse.shopping.data.product.entity.CartItemEntity
import woowacourse.shopping.data.shoppingCart.PageableCartItems

interface ShoppingCartDataSource {
    fun load(
        page: Int,
        size: Int,
    ): PageableCartItems

    fun addCartItem(
        productId: Long,
        quantity: Int,
    )

    fun remove(product: CartItemEntity)

    fun update(products: List<CartItemEntity>)

    fun updateCartItemQuantity(
        cartItemId: Long,
        quantity: Int,
    )

    fun quantity(): Int
}
