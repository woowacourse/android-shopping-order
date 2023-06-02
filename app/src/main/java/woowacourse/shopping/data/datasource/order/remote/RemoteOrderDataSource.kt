package woowacourse.shopping.data.datasource.order.remote

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.datasource.order.OrderDataSource
import woowacourse.shopping.data.remote.RetrofitModule
import woowacourse.shopping.data.remote.request.AddOrderRequest
import woowacourse.shopping.data.remote.response.addorder.AddOrderResponse

class RemoteOrderDataSource : OrderDataSource.Remote {
    override fun addOrder(
        basketProductsId: List<Int>,
        usingPoint: Int,
        orderTotalPrice: Int,
        onReceived: (AddOrderResponse) -> Unit
    ) {
        val addOrderRequest: AddOrderRequest = AddOrderRequest(
            cartIds = basketProductsId,
            point = usingPoint,
            totalPrice = orderTotalPrice
        )
        RetrofitModule.orderService.addOrder(addOrderRequest).enqueue(
            object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {}
                override fun onFailure(call: Call<Unit>, t: Throwable) {}
            }
        )
    }
}
