package woowacourse.shopping.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.datasource.remote.RemoteOrderDataSource
import woowacourse.shopping.data.network.request.OrderRequest
import woowacourse.shopping.domain.exception.NetworkResult
import woowacourse.shopping.domain.repository.OrderRepository

class DefaultOrderRepository(
    private val remoteOrderDataSource: RemoteOrderDataSource,
) : OrderRepository {
    override suspend fun createOrder(cartItemIds: List<Long>): NetworkResult<Unit> =
        withContext(Dispatchers.IO) {
            val request = OrderRequest(cartItemIds)
            remoteOrderDataSource.createOrder(request)
        }
}
