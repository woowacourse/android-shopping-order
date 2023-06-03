package woowacourse.shopping.data.order

import woowacourse.shopping.data.order.dto.Order
import woowacourse.shopping.data.order.dto.OrderCartItems
import woowacourse.shopping.data.order.dto.Orders

interface OrderDataSource {
    fun loadOrders(callback: (Orders?) -> Unit)
    fun loadOrder(orderId: Long, callback: (Order?) -> Unit)
    fun orderCartProducts(orderCartItems: OrderCartItems, callback: () -> Unit)
}
