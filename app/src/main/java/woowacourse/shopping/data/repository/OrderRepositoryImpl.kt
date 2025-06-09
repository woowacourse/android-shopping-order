package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.OrderDataSource
import woowacourse.shopping.data.model.request.OrderRequest

class OrderRepositoryImpl(
    private val orderDataSource: OrderDataSource,
) : OrderRepository {
    override suspend fun placeOrder(cartIds: List<Long>) {
        orderDataSource.submitOrder(OrderRequest(cartIds))
    }
}
