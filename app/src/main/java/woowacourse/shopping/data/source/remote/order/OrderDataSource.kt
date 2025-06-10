package woowacourse.shopping.data.source.remote.order

interface OrderDataSource {
    suspend fun createOrder(cartIds: List<Long>): Result<Unit>
}
