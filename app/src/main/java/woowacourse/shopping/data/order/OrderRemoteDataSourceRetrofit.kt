package woowacourse.shopping.data.order

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import woowacourse.shopping.data.entity.PayRequest
import woowacourse.shopping.data.entity.PayResponse
import woowacourse.shopping.data.server.OrderRemoteDataSource

class OrderRemoteDataSourceRetrofit(retrofit: Retrofit) : OrderRemoteDataSource {
    private val orderService: OrderService = retrofit.create(OrderService::class.java)

    override fun addOrder(order: PayRequest, onSuccess: (Int) -> Unit, onFailure: () -> Unit) {
        orderService.requestOrder(order).enqueue(object : Callback<PayResponse> {
            override fun onResponse(call: Call<PayResponse>, response: Response<PayResponse>) {
                if(response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!.orderId)
                }
                else {
                    onFailure()
                }
            }

            override fun onFailure(call: Call<PayResponse>, t: Throwable) {
                onFailure()
            }
        })
    }
}