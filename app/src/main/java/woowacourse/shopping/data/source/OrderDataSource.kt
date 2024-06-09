package woowacourse.shopping.data.source

interface OrderDataSource {
    suspend fun order2(cartItemIds: List<Long>): Result<Unit>

    suspend fun save2(
        cartItemId: Long,
        quantity: Int,
    ): Result<Unit>

    suspend fun load2(): Result<Map<Long, Int>>

    suspend fun allQuantity2(): Result<Int>

    suspend fun clear2(): Result<Unit>
}
