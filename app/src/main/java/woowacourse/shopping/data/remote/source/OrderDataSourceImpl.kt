package woowacourse.shopping.data.remote.source

import retrofit2.Call
import woowacourse.shopping.data.remote.api.ApiClient
import woowacourse.shopping.data.remote.api.OrderApiService
import woowacourse.shopping.data.remote.dto.cart.CartOrderRequest
import woowacourse.shopping.data.source.OrderDataSource

class OrderDataSourceImpl(apiClient: ApiClient) : OrderDataSource {
    private val orderApiService: OrderApiService =
        apiClient.createService(OrderApiService::class.java)

    override fun orderItems(ids: List<Int>): Call<Unit> {
        return orderApiService.orderItems(cartItemIds = CartOrderRequest(ids))
    }
}
