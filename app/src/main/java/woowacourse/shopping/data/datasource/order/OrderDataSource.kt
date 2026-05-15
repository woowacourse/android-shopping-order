package woowacourse.shopping.data.datasource.order

interface OrderDataSource {
    suspend fun orders(cartItemIds: List<String>)
}
