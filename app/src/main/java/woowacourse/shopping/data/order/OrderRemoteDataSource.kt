package woowacourse.shopping.data.order

import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.order.dto.Order
import woowacourse.shopping.data.order.dto.OrderCartItems
import woowacourse.shopping.data.order.dto.Orders

class OrderRemoteDataSource(
    baseUrl: String,
    private val userId: String,
) : OrderDataSource {
    private val retrofitService: OrderRetrofitService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OrderRetrofitService::class.java)

    override fun loadOrders(callback: (Orders?) -> Unit) {
        retrofitService.loadOrders(userId)
            .enqueue(
                object : retrofit2.Callback<Orders> {
                    override fun onResponse(
                        call: Call<Orders>,
                        response: Response<Orders>,
                    ) {
                        val orders = response.body()
                        callback(orders)
                    }

                    override fun onFailure(call: Call<Orders>, t: Throwable) {}
                },
            )
    }

    override fun loadOrder(orderId: Long, callback: (Order?) -> Unit) {
        retrofitService.loadOrder(userId, orderId)
            .enqueue(
                object : retrofit2.Callback<Order> {
                    override fun onResponse(
                        call: Call<Order>,
                        response: Response<Order>,
                    ) {
                        val order = response.body()
                        callback(order)
                    }

                    override fun onFailure(call: Call<Order>, t: Throwable) {}
                },
            )
    }

    override fun orderCartProducts(orderCartItems: OrderCartItems, callback: () -> Unit) {
        retrofitService.orderCartItems(userId, orderCartItems)
            .enqueue(
                object : retrofit2.Callback<Unit> {
                    override fun onResponse(
                        call: Call<Unit>,
                        response: Response<Unit>,
                    ) {
                        callback()
                    }

                    override fun onFailure(call: Call<Unit>, t: Throwable) {}
                },
            )
    }
}
