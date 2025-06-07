package woowacourse.shopping.data.repository.remote

import woowacourse.shopping.data.datasource.remote.OrderRemoteDataSource
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val orderRemoteDataSource: OrderRemoteDataSource,
) : OrderRepository {
    override suspend fun order(cartIds: List<Long>): Result<Unit> = orderRemoteDataSource.order(cartIds)
}
