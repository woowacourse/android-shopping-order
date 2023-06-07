package woowacouse.shopping.data.repository.order

import woowacouse.shopping.model.order.Order
import woowacouse.shopping.model.order.OrderDetail
import woowacouse.shopping.model.point.Point

interface OrderRepository {
    fun addOrder(
        orderInfo: Order,
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (Long) -> Unit,
    )

    fun loadOrder(
        orderId: Long,
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (OrderDetail) -> Unit,
    )

    fun loadOrderList(
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (List<OrderDetail>) -> Unit,
    )

    fun loadPoint(
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (Point) -> Unit,
    )

    fun loadPredictionSavePoint(
        orderPrice: Int,
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (Point) -> Unit,
    )
}
