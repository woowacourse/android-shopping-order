package woowacourse.shopping.data.respository.order

import woowacourse.shopping.data.respository.order.source.remote.OrderRemoteDataSource
import woowacouse.shopping.data.repository.order.OrderRepository
import woowacouse.shopping.model.order.Order
import woowacouse.shopping.model.order.OrderDetail

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

    override fun loadOrder(orderId: Long, onFailure: () -> Unit, onSuccess: (OrderDetail) -> Unit) {
        orderRemoteDataSource.requestOrderItem(orderId, onFailure, onSuccess)
    }

    override fun loadOrderList(onFailure: () -> Unit, onSuccess: (List<OrderDetail>) -> Unit) {
        orderRemoteDataSource.requestOrderList(onFailure, onSuccess)
    }
}
