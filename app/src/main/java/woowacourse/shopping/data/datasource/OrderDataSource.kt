package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.network.request.OrderRequest
import woowacourse.shopping.data.network.service.OrderService

class OrderDataSource(
    private val service: OrderService,
) {
    suspend fun createOrder(request: OrderRequest) {
        return service.createOrder(request)
    }
}
