package woowacourse.shopping.service

import woowacourse.shopping.model.OrderInfo
import woowacourse.shopping.utils.RetrofitUtil

class RemoteOrderService {
    private var credentials = "BASIC YUBhLmNvbToxMjM0"

    fun getOrderItemsInfo(ids: List<Int>, callback: (OrderInfo?) -> Unit) {
        RetrofitUtil.retrofitOrderService.orderCart(ids, credentials).enqueue(
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

    fun postOrderItem(ids: List<Int>, usedPoints: Int, callback: () -> Unit) {
        //
    }
}
