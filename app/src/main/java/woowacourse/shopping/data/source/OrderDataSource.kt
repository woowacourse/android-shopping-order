package woowacourse.shopping.data.source

interface OrderDataSource {
    fun order(cartItemIds: List<Long>)

    fun save(
        cartItemId: Long,
        quantity: Int,
    )

    fun load(): Map<Long, Int>

    fun allQuantity(): Int

    fun claer()

    suspend fun order2(cartItemIds: List<Long>): Result<Unit>

    suspend fun save2(
        cartItemId: Long,
        quantity: Int,
    ): Result<Unit>

    suspend fun load2(): Result<Map<Long, Int>>

    suspend fun allQuantity2(): Result<Int>

    suspend fun clear2(): Result<Unit>
}
