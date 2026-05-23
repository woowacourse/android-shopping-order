package woowacourse.shopping.data.repository

import woowacourse.shopping.data.source.remote.datasource.OrderRemoteDataSource
import woowacourse.shopping.domain.repository.OrderRepository

class DefaultOrderRepository(
    private val remoteDataSource: OrderRemoteDataSource,
) : OrderRepository {
    override suspend fun requestOrder(itemIds: List<Long>) {
        remoteDataSource.requestOrder(itemIds)
    }
}
