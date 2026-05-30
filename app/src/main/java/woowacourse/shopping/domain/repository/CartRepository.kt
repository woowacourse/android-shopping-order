package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.cart.CartItem
import woowacourse.shopping.domain.repository.query.CartPageResult

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
