package woowacourse.shopping.data.order

import woowacourse.shopping.domain.repository.order.OrderRepository

class OrderRemoteRepository(
    private val orderDataSource: OrderRemoteDataSource,
) : OrderRepository {
    override fun order(cartItemIds: List<Long>) {
        orderDataSource.order(cartItemIds)
    }
}
