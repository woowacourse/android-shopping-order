package woowacourse.shopping.repository

import woowacourse.shopping.model.CartItem
import woowacourse.shopping.repository.query.CartPageResult

interface CartRepository {
    suspend fun createOrder(cartItemIds: List<Long>): Result<Unit>

    suspend fun setQuantity(
        productId: Long,
        quantity: Int,
    ): Result<Unit>

    suspend fun getCartPage(
        page: Int,
        size: Int,
    ): Result<CartPageResult>

    suspend fun getCartItemsByProductIds(productIds: Set<Long>): Result<List<CartItem>>

    suspend fun count(): Result<Int>
}
