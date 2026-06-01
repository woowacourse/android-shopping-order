package woowacourse.shopping.data.repository.order

import woowacourse.shopping.data.datasource.remote.order.OrderRemoteDataSource
import woowacourse.shopping.data.remote.retrofit.dto.OrderInfo
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val orderRemoteDataSource: OrderRemoteDataSource,
) : OrderRepository {
    override suspend fun order(orderInfo: OrderInfo) {
        orderRemoteDataSource.order(orderInfo = orderInfo)
    }
}
