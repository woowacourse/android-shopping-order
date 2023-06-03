package woowacourse.shopping.data.repository

import woowacourse.shopping.model.Order
import woowacourse.shopping.model.OrderHistory

interface OrderRepository {
    fun getOrderList(cartIds: List<Int>, callback: (Result<Order>) -> Unit)
    fun getOrders(callback: (Result<List<OrderHistory>>) -> Unit)
    fun getOrder(id: Long, callback: (Result<OrderHistory>) -> Unit)
    fun postOrder(point: Int, cartIds: List<Int>, callback: (Result<Long>) -> Unit)
}
