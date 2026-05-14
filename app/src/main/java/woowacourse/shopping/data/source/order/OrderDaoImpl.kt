package woowacourse.shopping.data.source.order

import woowacourse.shopping.data.network.order.OrderService

class OrderDaoImpl(
    private val orderService: OrderService,
) : OrderDao {
    override suspend fun orders(cartItemIds: List<String>) {
        orderService.orders(
            cartItemIds = cartItemIds,
        )
    }
}
