package woowacourse.shopping.data.remoteDataSource

import woowacourse.shopping.model.Order
import woowacourse.shopping.model.OrderHistory

interface OrderRemoteDataSource {
    fun getOrder(cartIds: List<Int>): Result<Order>
    fun getOrderHistoriesNext(): Result<List<OrderHistory>>
    fun getOrderHistory(id: Long): Result<OrderHistory>
    fun postOrder(point: Int, cartIds: List<Int>): Result<Long>
}
