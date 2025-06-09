package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.OrderDataSource
import woowacourse.shopping.data.network.request.OrderRequest
import woowacourse.shopping.domain.repository.OrderRepository

class DefaultOrderRepository(
    private val dataSource: OrderDataSource,
) : OrderRepository {
    override suspend fun createOrder(ids: List<Long>) {
        return dataSource.createOrder(OrderRequest(ids))
    }
}
