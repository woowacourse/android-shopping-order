package woowacourse.shopping.data.source.remote.order

interface OrderDataSource {
    suspend fun orderProducts(ids: List<Long>): Result<Unit>
}
