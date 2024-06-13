package woowacourse.shopping.fake

import woowacourse.shopping.domain.repository.OrderRepository

class FakeOrderRepository : OrderRepository {
    override suspend fun createOrder(cartItemIds: List<Int>): Result<Unit> {
        return Result.success(Unit)
    }
}
