package woowacourse.shopping.data.datasource.order

interface OrderDataSource {
    suspend fun submitOrder(cartIds: List<Long>)
}
