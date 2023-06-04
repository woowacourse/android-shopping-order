package woowacourse.shopping.repository

import woowacourse.shopping.model.OrderInfo
import woowacourse.shopping.remoteService.RemoteOrderService

class OrderRepositoryImpl(
    private val remoteDatabase: RemoteOrderService,
) : OrderRepository {
    override fun getOrderInfo(ids: List<Int>, callback: (OrderInfo?) -> Unit) {
        remoteDatabase.getOrderItemsInfo(ids) {
            callback(it)
        }
    }

    override fun postOrder(ids: List<Int>, usedPoints: Int, callback: () -> Unit) {
        remoteDatabase.postOrderItem(ids, usedPoints) {
            callback()
        }
    }
}
