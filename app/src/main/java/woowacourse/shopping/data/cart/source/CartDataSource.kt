package woowacourse.shopping.data.cart.source

import woowacourse.shopping.data.cart.PageableCartItemData
import woowacourse.shopping.data.product.entity.CartItemEntity

interface CartDataSource {
    fun pageableCartItems(
        page: Int,
        size: Int,
    ): PageableCartItemData

    fun cart(): List<CartItemEntity>

    fun addCartItem(
        productId: Long,
        quantity: Int,
    )

    fun remove(cartItemId: Long)

    fun updateCartItemQuantity(
        cartItemId: Long,
        newQuantity: Int,
    )

    fun cartItemsSize(): Int
}
