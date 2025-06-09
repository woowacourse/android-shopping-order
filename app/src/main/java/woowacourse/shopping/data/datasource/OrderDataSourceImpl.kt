package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.request.OrderRequest
import woowacourse.shopping.data.service.OrderService

class OrderDataSourceImpl(
    private val orderService: OrderService,
) : OrderDataSource {
    override suspend fun submitOrder(orderRequest: OrderRequest) {
        orderService.postOrder(orderRequest)
    }
}
