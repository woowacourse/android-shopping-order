package woowacourse.shopping.data.repository

import woowacourse.shopping.data.remote.OrderRemoteDataSource
import woowacourse.shopping.model.Order
import woowacourse.shopping.model.OrderHistories
import woowacourse.shopping.model.OrderHistory

class OrderDefaultRepository(
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
