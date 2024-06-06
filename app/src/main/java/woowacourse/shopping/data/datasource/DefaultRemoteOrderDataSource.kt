package woowacourse.shopping.data.datasource

import retrofit2.Call
import woowacourse.shopping.data.model.CartItemIds
import woowacourse.shopping.data.remote.OrderService

class DefaultRemoteOrderDataSource(
    private val orderService: OrderService,
) : RemoteOrderDataSource {
    override suspend fun postOrder(cartItemIds: CartItemIds) {
        return orderService.postOrder(cartItemIds)
    }
}
