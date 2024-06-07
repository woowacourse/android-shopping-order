package woowacourse.shopping.data.remote.datasource

import woowacourse.shopping.data.datasource.OrderDataSource
import woowacourse.shopping.data.dto.OrderRequest
import woowacourse.shopping.data.remote.service.OrderService

class OrderDataSourceImpl(
    private val orderService: OrderService,
) : OrderDataSource {
    override suspend fun postOrder(orderRequest: OrderRequest): Result<Unit> {
        return runCatching {
            orderService.postOrder(orderRequest)
        }
    }
}
