package woowacourse.shopping.data.repositoryImpl

import woowacourse.shopping.data.remoteDataSource.OrderRemoteDataSource
import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.model.OrderList

class OrderRepositoryImpl(
    private val orderApi: OrderRemoteDataSource
) : OrderRepository {
    override fun getOrderList(callback: (Result<OrderList>) -> Unit) {
        orderApi.getAll(callback)
    }
}
