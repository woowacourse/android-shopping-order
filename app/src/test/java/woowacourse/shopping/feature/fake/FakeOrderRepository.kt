package woowacourse.shopping.feature.fake

import woowacourse.shopping.data.repository.order.OrderRepository

class FakeOrderRepository : OrderRepository {
    override suspend fun orders(cartItemIds: List<Long>) {
        TODO("Not yet implemented")
    }
}
