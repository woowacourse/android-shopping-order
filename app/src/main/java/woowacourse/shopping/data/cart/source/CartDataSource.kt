package woowacourse.shopping.data.cart.source

import woowacourse.shopping.data.cart.dto.CartResponse
import woowacourse.shopping.data.product.entity.CartItemEntity

interface CartDataSource {
    fun pagedCartItems(
        page: Int,
        size: Int,
    ): CartResponse?

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
