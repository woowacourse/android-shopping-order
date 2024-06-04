package woowacourse.shopping.remote.source

import android.util.Log
import woowacourse.shopping.data.source.OrderDataSource
import woowacourse.shopping.remote.model.OrderRequest
import woowacourse.shopping.remote.service.OrderApiService
import kotlin.concurrent.thread

class OrderRemoteDataSource(private val orderApiService: OrderApiService): OrderDataSource {
    override fun order(cartItemIds: List<Long>) {
        thread {
            val orderRequest = OrderRequest(cartItemIds)
            Log.d(TAG, "order: orderRequest=$orderRequest")
            orderApiService.createOrder(orderRequest).execute()
        }.join()
    }

    companion object {
        private const val TAG = "OrderRemoteDataSource"
    }
}

