package woowacourse.shopping.data.datasource

interface OrderDataSource {
    suspend fun submitOrder(cartIds: List<Long>)
}
