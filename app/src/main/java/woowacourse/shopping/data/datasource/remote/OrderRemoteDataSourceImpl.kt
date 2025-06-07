package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.model.order.OrderRequest
import woowacourse.shopping.data.service.OrderService
import woowacourse.shopping.data.util.safeApiCall

class OrderRemoteDataSourceImpl(
    private val orderService: OrderService,
) : OrderRemoteDataSource {
    override suspend fun postOrder(cartProductIds: OrderRequest): Result<Unit> =
        safeApiCall {
            orderService.postOrder(AUTHORIZATION_KEY, cartProductIds)
        }

    companion object {
        private const val AUTHORIZATION_KEY = "Basic ${BuildConfig.AUTHORIZATION_KEY}"
    }
}
