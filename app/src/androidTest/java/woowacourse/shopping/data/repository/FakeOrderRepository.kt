package woowacourse.shopping.data.repository

import woowacourse.shopping.domain.repository.OrderRepository

class FakeOrderRepository : OrderRepository {
    override suspend fun postOrder(cartItemIds: List<Int>): Result<Unit> {
        return runCatching { Unit }
    }
}
