package woowacourse.shopping.repositoryImpl

import woowacourse.shopping.model.OrderInfo
import woowacourse.shopping.repository.OrderRepository
import woowacourse.shopping.service.RemoteOrderService

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
