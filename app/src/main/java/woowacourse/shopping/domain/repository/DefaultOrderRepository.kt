package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.source.OrderDataSource

class DefaultOrderRepository(
    private val orderSource: OrderDataSource
) : OrderRepository {
    override fun order(cartItemIds: List<Long>) {
        orderSource.order(cartItemIds)
    }

}
