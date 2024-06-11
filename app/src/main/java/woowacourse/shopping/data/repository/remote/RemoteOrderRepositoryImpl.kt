package woowacourse.shopping.data.repository.remote

import woowacourse.shopping.data.remote.source.RemoteOrderDataSourceImpl
import woowacourse.shopping.data.source.OrderDataSource
import woowacourse.shopping.domain.repository.OrderRepository

class RemoteOrderRepositoryImpl(
    private val orderDataSource: OrderDataSource = RemoteOrderDataSourceImpl(),
) : OrderRepository {
    override suspend fun orderShoppingCart(ids: List<Int>): Result<Unit> = orderDataSource.orderItems(ids = ids)
}
