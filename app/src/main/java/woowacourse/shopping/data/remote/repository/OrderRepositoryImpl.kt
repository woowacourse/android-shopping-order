package woowacourse.shopping.data.remote.repository

import woowacourse.shopping.data.remote.datasource.order.OrderDataSource
import woowacourse.shopping.data.remote.dto.request.OrderRequest
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val orderDataSource: OrderDataSource,
) : OrderRepository {
    override suspend fun post(orderRequest: OrderRequest): Result<Unit> {
        return orderDataSource.post(orderRequest).mapCatching {
            it.body
        }
    }
}
