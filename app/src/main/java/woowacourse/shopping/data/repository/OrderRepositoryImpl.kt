package woowacourse.shopping.data.repository

import woowacourse.shopping.data.dataSource.OrderDataSource
import woowacourse.shopping.data.model.OrderRequest
import woowacourse.shopping.model.OrderHistory
import woowacourse.shopping.model.OrderInfo
import woowacourse.shopping.repository.OrderRepository

class OrderRepositoryImpl(
    private val remoteDatabase: OrderDataSource,
) : OrderRepository {
    override fun getOrderInfo(ids: List<Int>, callback: (OrderInfo?) -> Unit) {
        remoteDatabase.getOrderItemsInfo(ids) {
            callback(it)
        }
    }

    override fun postOrder(ids: List<Int>, usedPoints: Int, callback: () -> Unit) {
        remoteDatabase.postOrderItem(OrderRequest(ids, usedPoints)) {
            callback()
        }
    }

    override fun getOrderList(callback: (List<OrderHistory>?) -> Unit) {
        remoteDatabase.getOrderHistories {
            callback(it?.orders)
        }
    }
}
