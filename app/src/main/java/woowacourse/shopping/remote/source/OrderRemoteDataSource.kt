package woowacourse.shopping.remote.source

import woowacourse.shopping.data.source.OrderDataSource
import woowacourse.shopping.remote.model.OrderRequest
import woowacourse.shopping.remote.service.OrderApiService

class OrderRemoteDataSource(private val orderApiService: OrderApiService) : OrderDataSource {
    override fun order(cartItemIds: List<Long>) {
        val orderRequest = OrderRequest(cartItemIds)
        orderApiService.createOrder(orderRequest).execute()
    }

    companion object {
        private const val TAG = "OrderRemoteDataSource"
    }
}

