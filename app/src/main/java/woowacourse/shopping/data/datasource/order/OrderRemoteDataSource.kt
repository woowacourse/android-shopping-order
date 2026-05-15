package woowacourse.shopping.data.datasource.order

import woowacourse.shopping.data.network.order.OrderService

class OrderRemoteDataSource(
    private val orderService: OrderService,
) : OrderDataSource {
    override suspend fun orders(cartItemIds: List<String>) {
        orderService.orders(
            cartItemIds = cartItemIds,
        )
    }
}
