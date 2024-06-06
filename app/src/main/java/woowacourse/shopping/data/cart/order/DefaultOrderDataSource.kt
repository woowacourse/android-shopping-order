package woowacourse.shopping.data.cart.order

import woowacourse.shopping.remote.dto.request.OrderRequest
import woowacourse.shopping.remote.service.OrderService

class DefaultOrderDataSource(
    private val orderService: OrderService,
) : OrderDataSource {
    override suspend fun orderProducts(productIds: List<Long>): Result<Unit> {
        return runCatching {
            orderService.orderProducts(OrderRequest(productIds))
        }
    }
}
