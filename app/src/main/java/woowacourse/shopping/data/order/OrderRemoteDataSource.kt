package woowacourse.shopping.data.order

import android.util.Log
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import woowacourse.shopping.data.order.dto.Order
import woowacourse.shopping.data.order.dto.OrderCartItemDtos
import woowacourse.shopping.data.order.dto.Orders

class OrderRemoteDataSource(
    retrofit: Retrofit,
) : OrderDataSource {
    private val retrofitService: OrderRetrofitService =
        retrofit.create(OrderRetrofitService::class.java)

    override fun loadOrders(callback: (Orders?) -> Unit) {
        retrofitService.loadOrders()
            .enqueue(
                object : retrofit2.Callback<Orders> {
                    override fun onResponse(
                        call: Call<Orders>,
                        response: Response<Orders>,
                    ) {
                        val orders = response.body()
                        callback(orders)
                    }

                    override fun onFailure(call: Call<Orders>, t: Throwable) {
                        Log.e("Request Failed", t.toString())
                    }
                },
            )
    }

    override fun loadOrder(orderId: Long, callback: (Order?) -> Unit) {
        retrofitService.loadOrder(orderId)
            .enqueue(
                object : retrofit2.Callback<Order> {
                    override fun onResponse(
                        call: Call<Order>,
                        response: Response<Order>,
                    ) {
                        val order = response.body()
                        callback(order)
                    }

                    override fun onFailure(call: Call<Order>, t: Throwable) {
                        Log.e("Request Failed", t.toString())
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