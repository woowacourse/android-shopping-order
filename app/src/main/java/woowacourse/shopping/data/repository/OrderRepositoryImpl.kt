package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.order.OrderRemoteDataSource
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val dataSource: OrderRemoteDataSource,
) : OrderRepository {
    override suspend fun postOrderProducts(cartIds: List<Long>): Result<Unit> =
        runCatching {
            dataSource.postOrderProducts(cartIds)
        }
}
