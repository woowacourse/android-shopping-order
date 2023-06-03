package woowacourse.shopping.data.repositoryImpl

import woowacourse.shopping.data.remoteDataSource.OrderRemoteDataSource
import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.model.Order
import woowacourse.shopping.model.OrderHistory

class OrderRepositoryImpl(
    private val orderRemoteDataSource: OrderRemoteDataSource
) : OrderRepository {
    override fun getOrder(cartIds: List<Int>, callback: (Result<Order>) -> Unit) {
        orderRemoteDataSource.getOrder(cartIds, callback)
    }

    override fun getOrderHistoriesNext(callback: (Result<List<OrderHistory>>) -> Unit) {
        orderRemoteDataSource.getOrderHistoriesNext(callback)
    }

    override fun getOrderHistory(id: Long, callback: (Result<OrderHistory>) -> Unit) {
        orderRemoteDataSource.getOrderHistory(id, callback)
    }

    override fun postOrder(point: Int, cartIds: List<Int>, callback: (Result<Long>) -> Unit) {
        orderRemoteDataSource.postOrder(point, cartIds, callback)
    }
}
