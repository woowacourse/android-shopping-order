package woowacourse.shopping.data.order

import retrofit2.Call
import woowacourse.shopping.data.dto.request.OrderRequest
import woowacourse.shopping.data.remote.ApiClient

class RemoteOrderDataSource {
    private val orderApiService: OrderApiService =
        ApiClient.getApiClient().create(OrderApiService::class.java)

    fun requestOrder(cartItemIds: List<Int>): Call<Unit> {
        return orderApiService.requestOrder(OrderRequest(cartItemIds))
    }
}
