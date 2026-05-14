package woowacourse.shopping.repository

import woowacourse.shopping.model.CartItem
import woowacourse.shopping.repository.query.CartPageResult

interface CartRepository {
    suspend fun createOrder(cartItemIds: List<Long>)

    suspend fun setQuantity(
        productId: Long,
        quantity: Int,
    )

    suspend fun getCartPage(
        page: Int,
        size: Int,
    ): CartPageResult

    suspend fun getCartItemsByProductIds(productIds: Set<Long>): List<CartItem>

    suspend fun count(): Int
}
