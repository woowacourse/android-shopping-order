package woowacourse.shopping.data.dataSource

import woowacourse.shopping.data.model.OrderRequest
import woowacourse.shopping.data.service.RetrofitOrderService
import woowacourse.shopping.data.service.RetrofitUtil
import woowacourse.shopping.model.OrderInfo

class RemoteOrderDataSource(
    private val service: RetrofitOrderService = RetrofitUtil.retrofitOrderService,
) : OrderDataSource {
    private var credentials = "BASIC YUBhLmNvbToxMjM0"

    override fun getOrderItemsInfo(ids: List<Int>, callback: (OrderInfo?) -> Unit) {
        service.orderCart(ids, credentials).enqueue(
            object : retrofit2.Callback<OrderInfo> {
                override fun onResponse(
                    call: retrofit2.Call<OrderInfo>,
                    response: retrofit2.Response<OrderInfo>,
                ) {
                    callback(response.body())
                }

                override fun onFailure(call: retrofit2.Call<OrderInfo>, t: Throwable) {
                    callback(null)
                }
            },
        )
    }

    override fun postOrderItem(orderRequest: OrderRequest, callback: () -> Unit) {
        service.postOrderItem(orderRequest, credentials).enqueue(
            object : retrofit2.Callback<Unit> {
                override fun onResponse(
                    call: retrofit2.Call<Unit>,
                    response: retrofit2.Response<Unit>,
                ) {
                    callback()
                }

                override fun onFailure(call: retrofit2.Call<Unit>, t: Throwable) {
                    callback()
                }
            },
        )
    }
}
