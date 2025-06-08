package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.NetworkResultHandler
import woowacourse.shopping.data.network.request.OrderRequest
import woowacourse.shopping.data.network.service.OrderService

class RemoteOrderDataSource(
    private val service: OrderService,
    private val handler: NetworkResultHandler,
) {
    suspend fun createOrder(cartItemIds: OrderRequest): Result<Unit> =
        handler.handleResult {
            service.createOrder(cartItemIds)
        }
}
