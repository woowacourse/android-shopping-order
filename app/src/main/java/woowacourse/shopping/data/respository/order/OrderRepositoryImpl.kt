package woowacourse.shopping.data.respository.order

import woowacourse.shopping.data.respository.order.source.remote.OrderRemoteDataSource
import woowacouse.shopping.data.repository.order.OrderRepository
import woowacouse.shopping.model.order.Order

class OrderRepositoryImpl(
    private val orderRemoteDataSource: OrderRemoteDataSource,
) : OrderRepository {
    override fun addOrder(
        orderInfo: Order,
        onFailure: () -> Unit,
        onSuccess: (Long) -> Unit
    ) {
        orderRemoteDataSource.requestPostData(orderInfo, onFailure, onSuccess)
    }
}
