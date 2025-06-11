package woowacourse.shopping.data.repository

import woowacourse.shopping.data.dto.order.Orders
import woowacourse.shopping.di.DataSourceProvider

class OrderRepositoryImpl : OrderRepository {
    private val orderService = DataSourceProvider.orderService

    override suspend fun insertOrders(cartItemIds: List<Long>): Result<Unit> =
        runCatching {
            orderService.postOrders(Orders(cartItemIds))
        }
}
