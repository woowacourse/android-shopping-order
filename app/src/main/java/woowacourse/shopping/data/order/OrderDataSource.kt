package woowacourse.shopping.data.order

import woowacourse.shopping.data.order.dto.Order
import woowacourse.shopping.data.order.dto.OrderCartItemDtos
import woowacourse.shopping.data.order.dto.Orders

interface OrderDataSource {
    fun loadOrders(callback: (Orders?) -> Unit)
    fun loadOrder(orderId: Long, callback: (Order?) -> Unit)
    fun orderCartProducts(orderCartItems: OrderCartItemDtos, callback: (Long) -> Unit)
}
