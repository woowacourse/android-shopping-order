package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.remote.RemoteOrderDataSource
import woowacourse.shopping.data.network.request.OrderRequest
import woowacourse.shopping.domain.repository.OrderRepository

class DefaultOrderRepository(
    private val remoteOrderDataSource: RemoteOrderDataSource,
) : OrderRepository {
    override suspend fun createOrder(cartItemIds: List<Long>): Result<Unit> {
        val request = OrderRequest(cartItemIds)
        return remoteOrderDataSource.createOrder(request)
    }
}
