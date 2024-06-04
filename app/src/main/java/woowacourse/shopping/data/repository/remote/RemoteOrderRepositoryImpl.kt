package woowacourse.shopping.data.repository.remote

import woowacourse.shopping.data.remote.source.OrderDataSourceImpl
import woowacourse.shopping.data.source.OrderDataSource
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.utils.exception.LatchUtils.executeWithLatch
import woowacourse.shopping.utils.exception.ErrorEvent

class RemoteOrderRepositoryImpl(
    private val orderDataSource: OrderDataSource = OrderDataSourceImpl(),
) : OrderRepository {
    override fun orderShoppingCart(ids: List<Int>): Result<Unit> = orderDataSource.orderItems(ids = ids)
}
