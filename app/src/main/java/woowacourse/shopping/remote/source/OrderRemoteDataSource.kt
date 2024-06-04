package woowacourse.shopping.remote.source

import woowacourse.shopping.remote.service.OrderApiService
import kotlin.concurrent.thread

class OrderRemoteDataSource(private val orderApiService: OrderApiService) {
    fun order(cartItemIds: List<Long>) {
        thread {
            orderApiService.createOrder(OrderRequest(cartItemIds)).execute()
        }.join()
    }
}

data class OrderRequest(
    val cartItemsId: List<Long>
)