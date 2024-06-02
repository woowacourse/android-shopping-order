package woowacourse.shopping.data.order

import woowacourse.shopping.remote.order.OrderApiService
import woowacourse.shopping.remote.order.OrderRequest

class OrderRemoteDataSource(private val orderApiService: OrderApiService) {
    fun order(cartItemIds: List<Long>) {
        orderApiService.createOrder(OrderRequest(cartItemIds)).execute()
    }
}
