package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.cart.CartItemIds
import woowacourse.shopping.data.remote.OrderService

class DefaultRemoteOrderDataSource(
    private val orderService: OrderService,
) : RemoteOrderDataSource {
    override suspend fun postOrder(cartItemIds: CartItemIds) {
        return orderService.postOrder(cartItemIds)
    }
}
