package woowacourse.shopping.feature.fake

import woowacourse.shopping.data.repository.order.OrderRepository
import woowacourse.shopping.domain.OrderResult

class FakeOrderRepository(
    private val result: OrderResult = OrderResult.Success,
) : OrderRepository {

    private val _orderedItems = mutableListOf<List<String>>()
    val orderedItems: List<List<String>> get() = _orderedItems

    override suspend fun orders(cartItemIds: List<String>): OrderResult {
        _orderedItems.add(cartItemIds)
        return result
    }
}
