package woowacourse.shopping.data.repsoitory

import woowacourse.shopping.data.datasource.remote.OrderRemoteDataSource
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val orderRemoteDataSource: OrderRemoteDataSource,
) : OrderRepository {
    override suspend fun insertOrderByIds(cartItemIds: List<Int>): Result<Unit> =
        runCatching {
            orderRemoteDataSource.postOrderByIds(cartItemIds)
        }
}
