package woowacourse.shopping.repository

import woowacourse.shopping.Order
import woowacourse.shopping.OrderDetail
import woowacourse.shopping.OrderProducts

interface OrderRepository {
    fun getAllOrders(onSuccess: (List<Order>) -> Unit)
    fun getOrderDetail(orderId: Int, onSuccess: (OrderDetail?) -> Unit)
    fun addOrder(sendPoint: Int, orderProducts: OrderProducts, onSuccess: (String?) -> Unit)
}
