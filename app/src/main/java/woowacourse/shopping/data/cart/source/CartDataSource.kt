package woowacourse.shopping.data.cart.source

import woowacourse.shopping.data.cart.PagedCartItemData
import woowacourse.shopping.data.product.entity.CartItemEntity

interface CartDataSource {
    fun pagedCartItems(
        page: Int,
        size: Int,
    ): PagedCartItemData

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
