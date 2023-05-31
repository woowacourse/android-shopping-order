package woowacourse.shopping.data.repositoryImpl

import woowacourse.shopping.data.remoteDataSource.OrderRemoteDataSource
import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.model.OrderList

class OrderRepositoryImpl(
    private val orderRemoteDataSource: OrderRemoteDataSource
) : OrderRepository {
    override fun getOrderList(cartIds: List<Int>, callback: (Result<OrderList>) -> Unit) {
        orderRemoteDataSource.getOrder(cartIds, callback)
    }
}
