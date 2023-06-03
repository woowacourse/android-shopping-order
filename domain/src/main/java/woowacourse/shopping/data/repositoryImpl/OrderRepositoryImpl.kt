package woowacourse.shopping.data.repositoryImpl

import woowacourse.shopping.data.remoteDataSource.OrderRemoteDataSource
import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.model.Order
import woowacourse.shopping.model.OrderHistory

class OrderRepositoryImpl(
    private val orderRemoteDataSource: OrderRemoteDataSource
) : OrderRepository {
    override fun getOrderList(cartIds: List<Int>, callback: (Result<Order>) -> Unit) {
        orderRemoteDataSource.getOrderList(cartIds, callback)
    }

    override fun getOrders(callback: (Result<List<OrderHistory>>) -> Unit) {
        orderRemoteDataSource.getOrders(callback)
    }

    override fun getOrder(id: Long, callback: (Result<OrderHistory>) -> Unit) {
        orderRemoteDataSource.getOrder(id, callback)
    }

    override fun postOrder(point: Int, cartIds: List<Int>, callback: (Result<Long>) -> Unit) {
        orderRemoteDataSource.postOrder(point, cartIds, callback)
    }
}
