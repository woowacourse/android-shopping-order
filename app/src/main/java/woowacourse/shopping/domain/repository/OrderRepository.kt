package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Order

interface OrderRepository {
    suspend fun addOrder(cartItemIds: List<String>): Result<Unit>

    suspend fun fetchOrder(
        cartItemIds: List<Long>,
        couponId: Long?,
    ): Order
}
