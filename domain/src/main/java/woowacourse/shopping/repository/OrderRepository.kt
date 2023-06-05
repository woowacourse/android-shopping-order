package woowacourse.shopping.repository

import woowacourse.shopping.model.OrderHistory
import woowacourse.shopping.model.OrderInfo

interface OrderRepository {
    fun getOrderInfo(ids: List<Int>, callback: (OrderInfo?) -> Unit)
    fun postOrder(ids: List<Int>, usedPoints: Int, callback: () -> Unit)
    fun getOrderHistoryList(callback: (List<OrderHistory>?) -> Unit)
    fun getOrderHistory(id: Int, callback: (OrderHistory?) -> Unit)
}
