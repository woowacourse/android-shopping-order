package woowacouse.shopping.data.repository.order

import woowacouse.shopping.model.order.Order
import woowacouse.shopping.model.order.OrderDetail

interface OrderRepository {
    fun addOrder(
        orderInfo: Order,
        onFailure: () -> Unit,
        onSuccess: (Long) -> Unit,
    )

    fun loadOrder(
        orderId: Long,
        onFailure: () -> Unit,
        onSuccess: (OrderDetail) -> Unit,
    )

    fun loadOrderList(
        onFailure: () -> Unit,
        onSuccess: (List<OrderDetail>) -> Unit,
    )
}
