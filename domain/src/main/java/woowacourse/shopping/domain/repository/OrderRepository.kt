package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Order

interface OrderRepository {
    fun order(orderItems: List<CartProduct>, callback: (Result<Long>) -> Unit)
    fun fetchOrders(callback: (Result<List<Order>>) -> Unit)
    fun fetchOrder(id: Long, callback: (Result<Order>) -> Unit)
}
