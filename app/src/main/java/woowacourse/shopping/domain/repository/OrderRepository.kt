package woowacourse.shopping.domain.repository

interface OrderRepository {
    fun order(cartItemIds: List<Long>)

    fun saveOrderItem(
        cartItemId: Long,
        quantity: Int,
    )

    fun orderItems(): Map<Long, Int>

    fun allOrderItemsQuantity(): Int

    fun orderItemsTotalPrice(): Int

    suspend fun order2(cartItemIds: List<Long>): Result<Unit>

    suspend fun saveOrderItem2(
        cartItemId: Long,
        quantity: Int,
    ): Result<Unit>

    suspend fun orderItems2(): Result<Map<Long, Int>>

    suspend fun allOrderItemsQuantity2(): Result<Int>

    suspend fun orderItemsTotalPrice2(): Result<Int>
}
