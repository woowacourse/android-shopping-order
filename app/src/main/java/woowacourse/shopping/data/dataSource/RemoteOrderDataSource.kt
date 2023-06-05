package woowacourse.shopping.data.dataSource

import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.data.dto.OrderHistoryDto
import woowacourse.shopping.data.dto.OrderInfoDto
import woowacourse.shopping.data.dto.OrderListResponse
import woowacourse.shopping.data.dto.OrderRequest
import woowacourse.shopping.data.service.RetrofitClient
import woowacourse.shopping.data.service.RetrofitOrderService

class RemoteOrderDataSource(
    private val service: RetrofitOrderService = RetrofitClient.getInstance().retrofitOrderService,
) : OrderDataSource {
    override fun getOrderItemsInfo(ids: List<Int>, callback: (OrderInfoDto?) -> Unit) {
        service.orderCart(ids).enqueue(
            object : retrofit2.Callback<OrderInfoDto> {
                override fun onResponse(
                    call: Call<OrderInfoDto>,
                    response: Response<OrderInfoDto>,
                ) {
                    callback(response.body())
                }

                override fun onFailure(call: Call<OrderInfoDto>, t: Throwable) {
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

    override fun getOrderHistory(id: Int, callback: (OrderHistoryDto?) -> Unit) {
        service.getOrder(id).enqueue(
            object : retrofit2.Callback<OrderHistoryDto> {
                override fun onResponse(
                    call: Call<OrderHistoryDto>,
                    response: Response<OrderHistoryDto>,
                ) {
                    callback(response.body())
                }

                override fun onFailure(call: Call<OrderHistoryDto>, t: Throwable) {
                    callback(null)
                }
            },
        )
    }
}
