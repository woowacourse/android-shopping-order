package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.NetworkResultHandler
import woowacourse.shopping.data.network.request.OrderRequest
import woowacourse.shopping.data.network.service.OrderService
import woowacourse.shopping.domain.exception.NetworkResult

class OrderDataSource(
    private val service: OrderService,
    private val handler: NetworkResultHandler,
) {
    suspend fun createOrder(cartItemIds: OrderRequest): NetworkResult<Unit> =
        handler.execute {
            service.createOrder(cartItemIds)
        }
}
