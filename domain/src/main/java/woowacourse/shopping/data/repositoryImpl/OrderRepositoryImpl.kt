package woowacourse.shopping.data.repositoryImpl

import woowacourse.shopping.data.remoteDataSource.OrderRemoteDataSource
import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.model.Order
import woowacourse.shopping.model.OrderHistories
import woowacourse.shopping.model.OrderHistory

class OrderRepositoryImpl(
    private val orderRemoteDataSource: OrderRemoteDataSource
) : OrderRepository {
    override fun getOrder(cartIds: List<Int>): Result<Order> {
        return orderRemoteDataSource.getOrder(cartIds)
    }

    override fun getOrderHistoriesNext(lastOrderId: Long): Result<OrderHistories> {
        return orderRemoteDataSource.getOrderHistoriesNext(lastOrderId = lastOrderId)
    }

    override fun getOrderHistory(id: Long): Result<OrderHistory> {
        return orderRemoteDataSource.getOrderHistory(id)
    }

    override fun postOrder(point: Int, cartIds: List<Int>): Result<Long> {
        return orderRemoteDataSource.postOrder(point, cartIds)
    }
}
