package woowacourse.shopping.data.respository.order

import woowacourse.shopping.data.mapper.toModel
import woowacourse.shopping.data.respository.order.source.remote.OrderRemoteDataSource
import woowacouse.shopping.data.repository.order.OrderRepository
import woowacouse.shopping.model.order.Order
import woowacouse.shopping.model.order.OrderDetail
import woowacouse.shopping.model.point.Point

class OrderRepositoryImpl(
    private val orderRemoteDataSource: OrderRemoteDataSource,
) : OrderRepository {
    override fun addOrder(
        orderInfo: Order,
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (Long) -> Unit
    ) {
        orderRemoteDataSource.requestPostData(orderInfo, onFailure, onSuccess)
    }

    override fun loadOrder(
        orderId: Long,
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (OrderDetail) -> Unit
    ) {
        orderRemoteDataSource.requestOrderItem(orderId, onFailure) { orderDetail ->
            onSuccess(orderDetail.toModel())
        }
    }

    override fun loadOrderList(
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (List<OrderDetail>) -> Unit
    ) {
        orderRemoteDataSource.requestOrderList(onFailure) { orders ->
            onSuccess(orders.map { it.toModel() })
        }
    }

    override fun loadPoint(
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (Point) -> Unit
    ) {
        orderRemoteDataSource.requestPoint(onFailure) { point ->
            onSuccess(point.toModel())
        }
    }

    override fun loadPredictionSavePoint(
        orderPrice: Int,
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (Point) -> Unit
    ) {
        orderRemoteDataSource.requestPredictionSavePoint(orderPrice, onFailure) { point ->
            onSuccess(point.toModel())
        }
    }
}
