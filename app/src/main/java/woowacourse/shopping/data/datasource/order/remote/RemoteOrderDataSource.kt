package woowacourse.shopping.data.datasource.order.remote

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.datasource.order.OrderDataSource
import woowacourse.shopping.data.remote.RetrofitModule
import woowacourse.shopping.data.remote.request.AddOrderRequest
import woowacourse.shopping.data.remote.response.order.addorder.AddOrderErrorBody
import woowacourse.shopping.data.remote.response.order.addorder.AddOrderFailureException

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
}
