package woowacourse.shopping.data.order

import woowacourse.shopping.data.dto.request.OrderRequestDto
import woowacourse.shopping.data.remote.ApiClient

class RemoteOrderDataSource {
    private val orderApiService: OrderApiService =
        ApiClient.getApiClient().create(OrderApiService::class.java)

    suspend fun order(cartItemIds: List<Long>) {
        return orderApiService.requestOrder(OrderRequestDto(cartItemIds))
    }
}
