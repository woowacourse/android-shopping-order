package woowacouse.shopping.data.repository.order

import woowacouse.shopping.model.order.Order
import woowacouse.shopping.model.order.OrderDetail

interface OrderRepository {
    fun addOrder(
        orderInfo: Order,
        onFailure: (message: String) -> Unit,
        onSuccess: (Long) -> Unit,
    )

    fun loadOrder(
        orderId: Long,
        onFailure: (message: String) -> Unit,
        onSuccess: (OrderDetail) -> Unit,
    )

    fun loadOrderList(
        onFailure: (message: String) -> Unit,
        onSuccess: (List<OrderDetail>) -> Unit,
    )
}
