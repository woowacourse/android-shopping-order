package woowacourse.shopping.remote.order

import kotlin.concurrent.thread

class OrderRemoteDataSource(private val orderApiService: OrderApiService) {
    fun order(cartItemIds: List<Long>) {
        thread {
            orderApiService.createOrder(OrderRequest(cartItemIds)).execute()
        }.join()
    }
}
