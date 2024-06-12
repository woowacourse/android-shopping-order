package woowacourse.shopping.data.remote.datasource.order

import retrofit2.Response
import woowacourse.shopping.data.remote.dto.request.OrderRequest
import woowacourse.shopping.data.remote.service.OrderApi

class RetrofitOrderDataSource(
    private val orderApi: OrderApi = OrderApi.service(),
) : OrderDataSource {
    override suspend fun post(orderRequest: OrderRequest): Response<Unit> {
        return orderApi.postOrders(orderRequest = orderRequest)
    }
}
