package woowacourse.shopping.data.repository

import woowacourse.shopping.model.Order
import woowacourse.shopping.model.OrderHistory

interface OrderRepository {
    fun getOrder(cartIds: List<Int>): Result<Order>
    fun getOrderHistoriesNext(): Result<List<OrderHistory>>
    fun getOrderHistory(id: Long): Result<OrderHistory>
    fun postOrder(point: Int, cartIds: List<Int>): Result<Long>
}
