package woowacourse.shopping.data.repository

import woowacourse.shopping.remote.dto.OrderRequest
import woowacourse.shopping.remote.datasource.RemoteOrderDataSource
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val remoteOrderDataSource: RemoteOrderDataSource,
) : OrderRepository {
    override suspend fun postOrder(cartItemIds: List<Int>): Result<Unit> {
        return remoteOrderDataSource.postOrder(OrderRequest(cartItemIds))
    }
}
