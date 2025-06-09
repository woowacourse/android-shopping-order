package woowacourse.shopping.data.repository

import retrofit2.HttpException
import woowacourse.shopping.data.dto.order.Orders
import woowacourse.shopping.di.DataSourceProvider

class OrderRepositoryImpl : OrderRepository {
    private val orderService = DataSourceProvider.orderService

    override suspend fun insertOrders(cartItemIds: List<Long>): Result<Unit> =
        runCatching {
            val response = orderService.postOrders(Orders(cartItemIds))
            if (response.isSuccessful) {
                response.body()
            } else {
                throw HttpException(response)
            }
        }
}
