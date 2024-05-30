package woowacourse.shopping.data.remote

import retrofit2.Call
import woowacourse.shopping.data.datasource.OrderDataSource

class RemoteOrderDataSource(
    private val orderService: OrderService,
) : OrderDataSource {
    override fun postOrder(cartItemIds: CartItemIds): Call<Unit> {
        return orderService.postOrder(cartItemIds)
    }
}
