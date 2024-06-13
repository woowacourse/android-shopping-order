package woowacourse.shopping.remote.datasource

import woowacourse.shopping.data.datasource.remote.RemoteOrderDataSource
import woowacourse.shopping.remote.NetworkResult
import woowacourse.shopping.remote.api.ApiClient
import woowacourse.shopping.remote.api.OrderApiService
import woowacourse.shopping.remote.dto.request.OrderRequest
import woowacourse.shopping.remote.executeSafeApiCall

class RemoteOrderDataSourceImpl(
    private val orderApiService: OrderApiService =
        ApiClient.getApiClient().create(OrderApiService::class.java),
) : RemoteOrderDataSource {
    override suspend fun requestOrder(cartItemIds: List<Int>): NetworkResult<Unit> {
        val request = OrderRequest(cartItemIds)
        return executeSafeApiCall { orderApiService.requestOrder(request) }
    }
}
