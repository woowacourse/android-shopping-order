package woowacourse.shopping.data.datasource

import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.model.order.OrderRequest
import woowacourse.shopping.data.service.OrderService

class OrderRemoteDataSourceImpl(
    private val orderService: OrderService,
) : OrderRemoteDataSource {
    override suspend fun addOrder(cartItemIds: List<String>) = orderService.addOrder(AUTHORIZATION_KEY, OrderRequest(cartItemIds))

    companion object {
        private const val AUTHORIZATION_KEY = "Basic ${BuildConfig.AUTHORIZATION_KEY}"
    }
}
