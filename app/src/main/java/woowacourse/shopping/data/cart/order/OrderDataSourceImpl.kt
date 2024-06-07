package woowacourse.shopping.data.cart.order

import woowacourse.shopping.data.util.handleResponse
import woowacourse.shopping.remote.dto.request.OrderRequest
import woowacourse.shopping.remote.service.OrderService

class OrderDataSourceImpl(
    private val orderService: OrderService,
) : OrderDataSource {
    override suspend fun orderProducts(productIds: List<Long>): Result<Unit> {
        val response = orderService.orderProducts(OrderRequest(productIds))
        return handleResponse(response, Unit)
    }
}
