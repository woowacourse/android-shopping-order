package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.OrderRemoteDataSource
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val orderRemoteDataSource: OrderRemoteDataSource,
) : OrderRepository {
    override suspend fun addOrder(cartItemIds: List<String>): Result<Unit> = orderRemoteDataSource.addOrder(cartItemIds)
}
