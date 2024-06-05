package woowacourse.shopping.fake

import woowacourse.shopping.domain.repository.OrderRepository

class FakeOrderRepository : OrderRepository {
    override fun createOrder(
        cartItemIds: List<Int>,
        callback: (Result<Unit>) -> Unit,
    ) {
        callback(Result.success(Unit))
    }
}
