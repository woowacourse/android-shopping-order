package woowacourse.shopping.data.repository.order

import woowacourse.shopping.data.datasource.order.OrderDataSource

class OrderRepositoryImpl(
    private val orderDataSource: OrderDataSource,
) : OrderRepository {
    override suspend fun orders(cartItemIds: List<String>) {
        orderDataSource.orders(cartItemIds)
    }
}
