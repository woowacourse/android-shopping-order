package woowacourse.shopping.data.remote.source

import retrofit2.Call
import woowacourse.shopping.data.remote.api.NetworkManager
import woowacourse.shopping.data.remote.api.OrderApiService
import woowacourse.shopping.data.source.OrderDataSource

class OrderDataSourceImpl(
    private val orderApiService: OrderApiService = NetworkManager.orderService(),
): OrderDataSource {
    override fun orderItems(ids: List<Int>): Call<Unit> {
        return orderApiService.orderItems(cartItemIds = ids)
    }
}
