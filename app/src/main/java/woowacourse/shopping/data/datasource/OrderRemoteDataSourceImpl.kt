package woowacourse.shopping.data.datasource

import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.model.order.OrderRequest
import woowacourse.shopping.data.service.OrderService
import woowacourse.shopping.data.util.safeApiCall

class OrderRemoteDataSourceImpl(
    private val orderService: OrderService,
) : OrderRemoteDataSource {
    override fun addOrder(cartItemIds: List<String>) =
        safeApiCall {
            orderService.addOrder(AUTHORIZATION_KEY, OrderRequest(cartItemIds)).execute()
        }

    companion object {
        private const val AUTHORIZATION_KEY = "Basic ${BuildConfig.AUTHORIZATION_KEY}"
    }
}
