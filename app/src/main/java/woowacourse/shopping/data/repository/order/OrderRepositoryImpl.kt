package woowacourse.shopping.data.repository.order

import woowacourse.shopping.data.source.order.OrderDataSource

class OrderRepositoryImpl(
    private val orderDataSource: OrderDataSource,
) : OrderRepository {
    override suspend fun orders(cartItemIds: List<Long>) {
        orderDataSource.orders(cartItemIds)
    }
}
