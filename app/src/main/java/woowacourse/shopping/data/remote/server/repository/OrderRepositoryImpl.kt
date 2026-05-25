package woowacourse.shopping.data.remote.server.repository

import woowacourse.shopping.data.remote.server.dto.order.PostOrderRequest
import woowacourse.shopping.data.remote.server.service.OrderService
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val orderService: OrderService,
) : OrderRepository {
    override suspend fun createOrder(cartItemIds: List<Long>) {
        orderService.postOrder(PostOrderRequest(cartItemIds = cartItemIds))
    }
}
