package woowacourse.shopping.data.source.remote.order

interface OrderDataSource {
    suspend fun orderCheckedItems(cartIds: List<Long>): Result<Unit>
}
