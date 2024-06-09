package woowacourse.shopping.domain.repository

interface OrderRepository {
    suspend fun order(cartItemIds: List<Long>): Result<Unit>

    suspend fun saveOrderItem(
        cartItemId: Long,
        quantity: Int,
    ): Result<Unit>

    suspend fun orderItems(): Result<Map<Long, Int>>

    suspend fun allOrderItemsQuantity(): Result<Int>

    suspend fun orderItemsTotalPrice(): Result<Int>
}
