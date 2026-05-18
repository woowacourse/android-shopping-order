package woowacourse.shopping.data.repository.order

import woowacourse.shopping.data.datasource.order.OrderDataSource
import woowacourse.shopping.domain.OrderResult

class OrderRepositoryImpl(
    private val orderDataSource: OrderDataSource,
) : OrderRepository {
    override suspend fun orders(cartItemIds: List<String>): OrderResult = orderDataSource.orders(cartItemIds)
}
