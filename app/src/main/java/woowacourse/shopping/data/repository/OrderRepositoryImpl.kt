package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.remote.RemoteOrderDataSource
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(private val remoteOrderDataSource: RemoteOrderDataSource) : OrderRepository {
    override suspend fun completeOrder(cartItemIds: List<Long>): Result<Unit> {
        val cartIds = cartItemIds.map { it.toInt() }
        return runCatching {
            remoteOrderDataSource.requestOrder(cartIds)
        }
    }
}
