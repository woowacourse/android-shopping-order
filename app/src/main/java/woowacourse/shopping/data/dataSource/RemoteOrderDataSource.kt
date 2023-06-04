package woowacourse.shopping.data.dataSource

import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.data.model.OrderListResponse
import woowacourse.shopping.data.model.OrderRequest
import woowacourse.shopping.data.service.RetrofitClient
import woowacourse.shopping.data.service.RetrofitOrderService
import woowacourse.shopping.model.OrderInfo

class RemoteOrderDataSource(
    private val service: RetrofitOrderService = RetrofitClient.getInstance().retrofitOrderService,
) : OrderDataSource {
    override fun getOrderItemsInfo(ids: List<Int>, callback: (OrderInfo?) -> Unit) {
        service.orderCart(ids).enqueue(
            object : retrofit2.Callback<OrderInfo> {
                override fun onResponse(
                    call: Call<OrderInfo>,
                    response: Response<OrderInfo>,
                ) {
                    callback(response.body())
                }

                override fun onFailure(call: Call<OrderInfo>, t: Throwable) {
                    callback(null)
                }
            },
        )
    }

    override fun postOrderItem(orderRequest: OrderRequest, callback: () -> Unit) {
        service.postOrderItem(orderRequest).enqueue(
            object : retrofit2.Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    callback()
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    callback()
                }
            },
        )
    }

    override fun getOrderHistories(callback: (OrderListResponse?) -> Unit) {
        service.getOrders().enqueue(
            object : retrofit2.Callback<OrderListResponse> {
                override fun onResponse(
                    call: Call<OrderListResponse>,
                    response: Response<OrderListResponse>,
                ) {
                    callback(response.body())
                }

                override fun onFailure(call: Call<OrderListResponse>, t: Throwable) {
                    callback(null)
                }
            },
        )
    }
}
