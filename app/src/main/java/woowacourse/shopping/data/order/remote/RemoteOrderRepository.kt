package woowacourse.shopping.data.order.remote

import woowacourse.shopping.data.remote.RetrofitClient.orderApi
import woowacourse.shopping.domain.repository.OrderRepository

object RemoteOrderRepository : OrderRepository {
    override suspend fun createOrder(cartItemIds: List<Int>): Result<Unit> {
        return orderApi.requestCreateOrder(createOrderRequest = CreateOrderRequest(cartItemIds))
    }
}
