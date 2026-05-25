package woowacourse.shopping.data.source.order

import woowacourse.shopping.data.network.order.OrderService
import woowacourse.shopping.data.network.order.dto.OrderRequestDto

class OrderServerDataSourceImpl(
    private val orderService: OrderService,
) : OrderDataSource {
    override suspend fun orders(cartItemIds: List<Long>) {
        orderService.orders(
            orderRequest = OrderRequestDto(cartItemIds = cartItemIds),
        )
    }
}
