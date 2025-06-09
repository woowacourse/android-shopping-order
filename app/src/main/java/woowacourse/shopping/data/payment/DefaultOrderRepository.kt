package woowacourse.shopping.data.payment

import woowacourse.shopping.data.API
import woowacourse.shopping.domain.payment.OrderRepository

class DefaultOrderRepository(
    private val service: OrderService = API.orderService,
) : OrderRepository {
    override suspend fun order(cartItemIds: List<Long>) = runCatching { service.postOrder(OrderRequest(cartItemIds)) }
}
