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
        onFailure: (message: String) -> Unit,
        onSuccess: (Long) -> Unit
    ) {
        orderRemoteDataSource.requestPostData(orderInfo, onFailure, onSuccess)
    }

    override fun loadOrder(
        orderId: Long,
        onFailure: (message: String) -> Unit,
        onSuccess: (OrderDetail) -> Unit
    ) {
        orderRemoteDataSource.requestOrderItem(orderId, onFailure, onSuccess)
    }

    override fun loadOrderList(
        onFailure: (message: String) -> Unit,
        onSuccess: (List<OrderDetail>) -> Unit
    ) {
        orderRemoteDataSource.requestOrderList(onFailure, onSuccess)
    }
}
