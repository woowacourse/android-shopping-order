package woowacourse.shopping.data.repository.remote

import woowacourse.shopping.data.remote.source.OrderDataSourceImpl
import woowacourse.shopping.data.source.OrderDataSource
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.utils.exception.LatchUtils.executeWithLatch
import woowacourse.shopping.utils.exception.OrderException
import woowacourse.shopping.view.model.event.ErrorEvent

class RemoteOrderRepositoryImpl(
    private val orderDataSource: OrderDataSource = OrderDataSourceImpl(),
) : OrderRepository {
    override fun orderShoppingCart(ids: List<Int>) {
        executeWithLatch {
            val response = orderDataSource.orderItems(ids = ids).execute()
            if (!response.isSuccessful) throw OrderException(ErrorEvent.OrderEvent.OrderItemsEvent)
        }
    }
}
