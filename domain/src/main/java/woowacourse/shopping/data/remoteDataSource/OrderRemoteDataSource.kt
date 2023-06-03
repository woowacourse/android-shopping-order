package woowacourse.shopping.data.remoteDataSource

import woowacourse.shopping.model.Order
import woowacourse.shopping.model.OrderHistory

interface OrderRemoteDataSource {
    fun getOrder(cartIds: List<Int>, callback: (Result<Order>) -> Unit)
    fun getOrderHistoriesNext(callback: (Result<List<OrderHistory>>) -> Unit)
    fun getOrderHistory(id: Long, callback: (Result<OrderHistory>) -> Unit)
    fun postOrder(point: Int, cartIds: List<Int>, callback: (Result<Long>) -> Unit)
}
