package woowacourse.shopping.data.source.order

interface OrderDataSource {
    suspend fun orders(cartItemIds: List<Long>)
}
