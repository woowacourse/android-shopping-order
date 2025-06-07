package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.remote.OrderRemoteDataSource
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val orderRemoteDataSource: OrderRemoteDataSource,
) : OrderRepository {
    override suspend fun submitOrder(cartItemIds: List<Int>): Result<Unit> {
        return orderRemoteDataSource.submitOrder(cartItemIds)
    }
}
