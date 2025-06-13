package woowacourse.shopping.data.source

import android.util.Log
import woowacourse.shopping.data.dto.order.OrderRequest
import woowacourse.shopping.data.service.OrderService
import woowacourse.shopping.data.service.RetrofitProductService

class OrderRemoteDataSource(
    private val retrofitService: OrderService =
        RetrofitProductService.INSTANCE.create(
            OrderService::class.java,
        ),
) : OrderDataSource {
    override suspend fun orderProducts(orderProducts: List<Int>): Boolean {
        val request = OrderRequest(orderProducts)
        val response = retrofitService.postOrder(request)

        if (!response.isSuccessful) {
            Log.d("error", "error : $response")
            return false
        }

        return true
    }
}
