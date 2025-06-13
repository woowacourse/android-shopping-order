package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.OrderDataSource

class OrderRepositoryImpl(
    private val orderDataSource: OrderDataSource,
) : OrderRepository {
    override suspend fun placeOrder(cartIds: List<Long>) {
        orderDataSource.submitOrder(cartIds)
    }
}
