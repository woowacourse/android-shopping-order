package woowacourse.shopping.data.repository.order

import woowacourse.shopping.data.source.order.OrderDao

class OrderRepositoryImpl(
    private val orderDao: OrderDao,
) : OrderRepository {
    override suspend fun orders(cartItemIds: List<Long>) {
        orderDao.orders(cartItemIds)
    }
}
