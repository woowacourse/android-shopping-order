package woowacourse.shopping.data.source

interface OrderDataSource {
    suspend fun orderItems(ids: List<Int>): Result<Unit>
}
