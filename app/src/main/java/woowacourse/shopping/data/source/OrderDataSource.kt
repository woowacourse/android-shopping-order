package woowacourse.shopping.data.source

interface OrderDataSource {
    suspend fun order(cartItemIds: List<Long>): Result<Unit>

    suspend fun save(
        cartItemId: Long,
        quantity: Int,
    ): Result<Unit>

    suspend fun load(): Result<Map<Long, Int>>

    suspend fun allQuantity(): Result<Int>

    suspend fun clear(): Result<Unit>
}
