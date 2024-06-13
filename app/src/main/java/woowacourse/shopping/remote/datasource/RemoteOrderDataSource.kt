package woowacourse.shopping.remote.datasource

import woowacourse.shopping.data.datasource.OrderDataSource
import woowacourse.shopping.remote.dto.OrderRequest
import woowacourse.shopping.remote.service.OrderService

class RemoteOrderDataSource(
    private val orderService: OrderService,
) : OrderDataSource {
    override suspend fun postOrder(orderRequest: OrderRequest): Result<Unit> {
        return runCatching {
            orderService.postOrder(orderRequest)
        }
    }
}
