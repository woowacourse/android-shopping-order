package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.remote.OrderRemoteDataSource
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val remoteDataSource: OrderRemoteDataSource,
) : OrderRepository {
    override suspend fun createOrder(cartIds: List<Int>): Result<Unit> = remoteDataSource.insert(cartIds)
}
