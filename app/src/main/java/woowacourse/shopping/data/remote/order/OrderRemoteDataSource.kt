package woowacourse.shopping.data.remote.order

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.ApiClient
import woowacourse.shopping.data.common.BaseResponse
import woowacourse.shopping.data.common.SharedPreferencesDb
import woowacourse.shopping.data.remote.order.requestbody.OrderCartRequestBody
import woowacourse.shopping.data.remote.order.requestbody.OrderRequestBody
import woowacourse.shopping.data.remote.order.response.OrderDataModel
import woowacourse.shopping.data.remote.order.response.OrderDetailDataModel
import woowacourse.shopping.data.remote.order.response.OrderRequestDataModel

class OrderRemoteDataSource(private val sharedPreferences: SharedPreferencesDb) : OrderDataSource {
    private val orderClient = ApiClient.client.create(OrderService::class.java)

    override fun order(
        orderRequest: OrderRequestDataModel,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        orderClient.order(
            OrderRequestBody(
                orderRequest.spendPoint,
                orderRequest.orderItems.map {
                    OrderCartRequestBody(
                        it.productId,
                        it.quantity
                    )
                }
            )
        ).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                onSuccess()
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onFailure()
            }
        })
    }

    override fun loadOrderList(onSuccess: (List<OrderDataModel>) -> Unit, onFailure: () -> Unit) {
        orderClient.loadOrderList()
            .enqueue(object : Callback<BaseResponse<List<OrderDataModel>>> {
                override fun onResponse(
                    call: Call<BaseResponse<List<OrderDataModel>>>,
                    response: Response<BaseResponse<List<OrderDataModel>>>
                ) {
                    onSuccess(response.body()?.result ?: emptyList())
                }

                override fun onFailure(
                    call: Call<BaseResponse<List<OrderDataModel>>>,
                    t: Throwable
                ) {
                }
            })
    }

    override fun loadOrderDetail(
        id: Int,
        onSuccess: (OrderDetailDataModel) -> Unit,
        onFailure: () -> Unit
    ) {
        orderClient.loadOrderDetail(id)
            .enqueue(object : Callback<BaseResponse<OrderDetailDataModel>> {
                override fun onResponse(
                    call: Call<BaseResponse<OrderDetailDataModel>>,
                    response: Response<BaseResponse<OrderDetailDataModel>>
                ) {
                    val responseBody = response.body()
                    if (responseBody == null) {
                        onFailure()
                        return
                    }
                    onSuccess(responseBody.result)
                }

                override fun onFailure(
                    call: Call<BaseResponse<OrderDetailDataModel>>,
                    t: Throwable
                ) {
                }
            })
    }
}
