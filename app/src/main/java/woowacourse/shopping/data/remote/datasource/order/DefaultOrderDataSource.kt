package woowacourse.shopping.data.remote.datasource.order

import retrofit2.Response
import woowacourse.shopping.data.remote.dto.request.OrderRequest
import woowacourse.shopping.data.remote.service.OrderApi

class DefaultOrderDataSource(
    private val orderApi: OrderApi = OrderApi.service(),
    ): OrderDataSource {
    override suspend fun postOrders(orderRequest: OrderRequest): Response<Unit> {
        return orderApi.postOrders(orderRequest = orderRequest)
    }

}