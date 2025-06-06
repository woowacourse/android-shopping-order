package woowacourse.shopping.data.cart.source

import woowacourse.shopping.data.cart.PageableCartItemData
import woowacourse.shopping.data.product.entity.CartItemEntity

interface CartDataSource {
    suspend fun pageableCartItems(
        page: Int,
        size: Int,
    ): PageableCartItemData

    suspend fun cart(): List<CartItemEntity>

    suspend fun addCartItem(
        productId: Long,
        quantity: Int,
    ): Long?

    suspend fun remove(cartItemId: Long)

    suspend fun updateCartItemQuantity(
        cartItemId: Long,
        newQuantity: Int,
    )

    suspend fun cartItemsSize(): Int
}
