package woowacourse.shopping.data.source.remote.order

import woowacourse.shopping.data.model.OrderRequest
import woowacourse.shopping.data.source.remote.api.OrderApiService
import woowacourse.shopping.data.source.remote.util.safeApiCall

class OrderRemoteDataSource(
    private val api: OrderApiService
) : OrderDataSource {
    override suspend fun orderProducts(ids: List<Long>): Result<Unit> = safeApiCall {
        val orderRequest = OrderRequest(ids)
        api.order(request = orderRequest)
    }
}