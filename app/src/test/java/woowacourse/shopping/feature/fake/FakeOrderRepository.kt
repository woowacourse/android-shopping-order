package woowacourse.shopping.feature.fake

import woowacourse.shopping.data.repository.order.OrderRepository

class FakeOrderRepository : OrderRepository {

    private val _orderedItems = mutableListOf<List<String>>()
    val orderedItems: List<List<String>> get() = _orderedItems

    override suspend fun orders(cartItemIds: List<String>) {
        _orderedItems.add(cartItemIds)
    }
}
