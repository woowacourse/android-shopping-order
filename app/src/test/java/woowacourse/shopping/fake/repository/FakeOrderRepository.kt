package woowacourse.shopping.fake.repository

import okio.IOException
import woowacourse.shopping.domain.repository.OrderRepository

class FakeOrderRepository(
    var shouldFail: Boolean = false,
) : OrderRepository {
    var lastRequestedIds: List<Long>? = null
        private set

    override suspend fun requestOrder(itemIds: List<Long>) {
        if (shouldFail) throw IOException()
        lastRequestedIds = itemIds
    }
}
