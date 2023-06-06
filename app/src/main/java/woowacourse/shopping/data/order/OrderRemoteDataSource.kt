package woowacourse.shopping.data.order

import android.util.Log
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import woowacourse.shopping.data.order.dto.Order
import woowacourse.shopping.data.order.dto.OrderCartItemDtos
import woowacourse.shopping.data.order.dto.Orders
import woowacourse.shopping.data.util.RetrofitCallback

class OrderRemoteDataSource(
    retrofit: Retrofit,
) : OrderDataSource {
    private val retrofitService: OrderRetrofitService =
        retrofit.create(OrderRetrofitService::class.java)

    override fun loadOrders(callback: (Orders?) -> Unit) {
        retrofitService.loadOrders()
            .enqueue(
                object : RetrofitCallback<Orders>() {
                    override fun onSuccess(response: Orders?) {
                        callback(response)
                    }
                },
            )
    }

    override fun loadOrder(orderId: Long, callback: (Order?) -> Unit) {
        retrofitService.loadOrder(orderId)
            .enqueue(
                object : RetrofitCallback<Order>() {
                    override fun onSuccess(response: Order?) {
                        callback(response)
                    }
                },
            )
    }

    override fun orderCartProducts(orderCartItems: OrderCartItemDtos, callback: (Long) -> Unit) {
        retrofitService.orderCartItems(orderCartItems)
            .enqueue(
                object : retrofit2.Callback<Unit> {
                    override fun onResponse(
                        call: Call<Unit>,
                        response: Response<Unit>,
                    ) {
                        val location = response.headers()[LOCATION]?.split("/")?.last()
                        callback(location?.toLong() ?: 0)
                    }

                    override fun onFailure(call: Call<Unit>, t: Throwable) {
                        Log.e("Request Failed", t.toString())
                    }
                },
            )
    }

    companion object {
        private const val LOCATION = "Location"
    }
}
