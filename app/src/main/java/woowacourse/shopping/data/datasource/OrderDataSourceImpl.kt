package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.request.OrderRequest
import woowacourse.shopping.data.service.OrderService
import woowacourse.shopping.data.util.safeApiCall

class OrderDataSourceImpl(
    private val orderService: OrderService,
) : OrderDataSource {
    override suspend fun postOrder(request: OrderRequest): Result<Unit> =
        safeApiCall {
            orderService.postOrder(request)
        }
}
