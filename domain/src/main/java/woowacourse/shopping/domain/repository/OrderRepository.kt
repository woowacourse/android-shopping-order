package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.util.WoowaResult

interface OrderRepository {
    fun order(orderItems: List<CartProduct>, callback: (WoowaResult<Long>) -> Unit)
    fun fetchOrders(callback: (WoowaResult<List<Order>>) -> Unit)
    fun fetchOrder(id: Long, callback: (WoowaResult<Order>) -> Unit)
}
