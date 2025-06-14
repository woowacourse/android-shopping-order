package woowacourse.shopping.data.cart.source

import woowacourse.shopping.data.cart.dto.CartResponse
import woowacourse.shopping.data.product.entity.CartItemEntity

interface CartDataSource {
    suspend fun pagedCartItems(
        page: Int,
        size: Int,
    ): CartResponse?

    suspend fun cart(): List<CartItemEntity>

    suspend fun addCartItem(
        productId: Long,
        quantity: Int,
    )

    suspend fun remove(cartItemId: Long)

    suspend fun updateCartItemQuantity(
        cartItemId: Long,
        newQuantity: Int,
    )

    suspend fun cartItemsSize(): Int
}
