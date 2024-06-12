package woowacourse.shopping.data.remote.datasource.order

import retrofit2.Response
import woowacourse.shopping.data.remote.dto.Message
import woowacourse.shopping.data.remote.dto.request.OrderRequest
import woowacourse.shopping.data.remote.service.OrderApi

class RetrofitOrderDataSource(
    private val orderApi: OrderApi = OrderApi.service(),
) : OrderDataSource {
    override suspend fun post(orderRequest: OrderRequest): Result<Message<Unit>> = runCatching {
        val response = orderApi.postOrders(orderRequest = orderRequest)
        Message(
            code = response.code(),
            body = response.body()
        )
    }
}
