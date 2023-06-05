package woowacourse.shopping.data.datasource.order.remote

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.datasource.order.OrderDataSource
import woowacourse.shopping.data.httpclient.RetrofitModule
import woowacourse.shopping.data.httpclient.mapper.toData
import woowacourse.shopping.data.httpclient.request.AddOrderRequest
import woowacourse.shopping.data.httpclient.response.order.Individualorder.IndividualOrderResponse
import woowacourse.shopping.data.httpclient.response.order.addorder.AddOrderErrorBody
import woowacourse.shopping.data.httpclient.response.order.addorder.AddOrderFailureException
import woowacourse.shopping.data.model.DataOrder

class RemoteOrderDataSource : OrderDataSource.Remote {
    override fun addOrder(
        basketProductsId: List<Int>,
        usingPoint: Int,
        orderTotalPrice: Int,
        onReceived: (Result<Int>) -> Unit
    ) {
        val addOrderRequest: AddOrderRequest = AddOrderRequest(
            cartIds = basketProductsId,
            point = usingPoint,
            totalPrice = orderTotalPrice
        )
        RetrofitModule.orderService.addOrder(addOrderRequest).enqueue(
            object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful) {
                        val orderId = response.headers()["Location"]?.split("/")?.last()?.toInt()
                        orderId?.let { onReceived(Result.success(it)) }
                    } else {
                        val errorData = response.errorBody()?.let {
                            RetrofitModule.retrofit.responseBodyConverter<AddOrderErrorBody>(
                                AddOrderErrorBody::class.java,
                                AddOrderErrorBody::class.java.annotations
                            ).convert(it)
                        }

                        errorData?.let {
                            onReceived(Result.failure(AddOrderFailureException(addOrderErrorBody = it)))
                        }
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {}
            }
        )
    }

    override fun getIndividualOrderInfo(orderId: Int, onReceived: (DataOrder) -> Unit) {
        RetrofitModule.orderService.getIndividualOrderInfo(orderId).enqueue(
            object : Callback<IndividualOrderResponse> {
                override fun onResponse(
                    call: Call<IndividualOrderResponse>,
                    response: Response<IndividualOrderResponse>
                ) {
                    val orderInfo = response.body()?.toData()

                    orderInfo?.let {
                        onReceived(it)
                    }
                }

                override fun onFailure(call: Call<IndividualOrderResponse>, t: Throwable) {}
            }
        )
    }

    override fun getOrdersInfo(onReceived: (List<DataOrder>) -> Unit) {
        RetrofitModule.orderService.getOrdersInfo().enqueue(
            object : Callback<List<IndividualOrderResponse>> {
                override fun onResponse(
                    call: Call<List<IndividualOrderResponse>>,
                    response: Response<List<IndividualOrderResponse>>
                ) {
                    val ordersInfo = response.body()?.map { it.toData() }

                    ordersInfo?.let {
                        onReceived(it)
                    }
                }

                override fun onFailure(call: Call<List<IndividualOrderResponse>>, t: Throwable) {}
            }
        )
    }
}
