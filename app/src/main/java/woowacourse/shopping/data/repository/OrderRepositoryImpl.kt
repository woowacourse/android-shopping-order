package woowacourse.shopping.data.repository

import woowacourse.shopping.data.source.remote.order.OrderDataSource
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val orderDataSource: OrderDataSource,
) : OrderRepository {
    override suspend fun orderItems(cartIds: List<Long>): Result<Unit> {
        return orderDataSource.orderCheckedItems(cartIds)
    }
}
