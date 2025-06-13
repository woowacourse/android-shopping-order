package woowacourse.shopping.data.datasource.order

import woowacourse.shopping.data.model.request.OrderRequest
import woowacourse.shopping.data.service.OrderService

class OrderDataSourceImpl(
    private val orderService: OrderService,
) : OrderDataSource {
    override suspend fun submitOrder(cartIds: List<Long>) {
        orderService.postOrder(OrderRequest(cartIds))
    }
}
