package woowacourse.shopping.data.repository

import woowacourse.shopping.model.Order
import woowacourse.shopping.model.OrderList

interface OrderRepository {
    fun getOrderList(cartIds: List<Int>, callback: (Result<OrderList>) -> Unit)
    fun getOrders(callback: (Result<List<Order>>) -> Unit)
    fun getOrder(id: Long, callback: (Result<Order>) -> Unit)
    fun postOrder(point: Int, cartIds: List<Int>, callback: (Result<Long>) -> Unit)
}
