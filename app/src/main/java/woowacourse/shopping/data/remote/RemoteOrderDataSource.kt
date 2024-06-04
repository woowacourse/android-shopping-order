package woowacourse.shopping.data.remote

import retrofit2.Call
import woowacourse.shopping.data.datasource.OrderDataSource
import woowacourse.shopping.data.dto.OrderRequest

class RemoteOrderDataSource(
    private val orderService: OrderService,
) : OrderDataSource {
    override fun postOrder(orderRequest: OrderRequest): Call<Unit> {
        return orderService.postOrder(orderRequest)
    }
}
