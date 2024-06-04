package woowacourse.shopping.data.remote.source

import woowacourse.shopping.data.remote.api.NetworkManager
import woowacourse.shopping.data.remote.api.OrderApiService
import woowacourse.shopping.data.remote.dto.cart.CartOrderRequest
import woowacourse.shopping.data.source.OrderDataSource


class OrderDataSourceImpl(
    private val orderApiService: OrderApiService = NetworkManager.orderService(),
) : OrderDataSource {
    override fun orderItems(ids: List<Int>): Result<Unit> = runCatching {
        orderApiService.orderItems(cartItemIds = CartOrderRequest(ids))
    }
}
