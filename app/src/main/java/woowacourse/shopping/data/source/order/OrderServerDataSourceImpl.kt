package woowacourse.shopping.data.source.order

import woowacourse.shopping.data.network.order.OrderService

class OrderServerDataSourceImpl(
    private val orderService: OrderService,
) : OrderDataSource {
    override suspend fun orders(cartItemIds: List<Long>) {
        orderService.orders(
            cartItemIds = cartItemIds,
        )
    }
}
