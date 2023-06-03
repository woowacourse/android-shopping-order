package woowacourse.shopping.data.repository

import android.util.Log
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.retrofit.OrderApi
import woowacourse.shopping.domain.model.OrderCartItemsDTO
import woowacourse.shopping.domain.model.OrderDTO
import woowacourse.shopping.domain.model.OrdersDTO
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRemoteRepository(baseUrl: String) : OrderRepository {

    private val retrofitService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OrderApi::class.java)

    override fun getAll(callback: (OrdersDTO) -> Unit) {
        retrofitService.requestOrders().enqueue(object : retrofit2.Callback<OrdersDTO> {
            override fun onResponse(
                call: Call<OrdersDTO>,
                response: Response<OrdersDTO>,
            ) {
                response.body()?.let {
                    callback(it)
                }
            }

            override fun onFailure(call: Call<OrdersDTO>, t: Throwable) {
                throw t
            }
        })
    }

    override fun getOrder(id: Int, callback: (OrderDTO) -> Unit) {
        retrofitService.requestOrderDetail(id).enqueue(object : retrofit2.Callback<OrderDTO> {
            override fun onResponse(
                call: Call<OrderDTO>,
                response: Response<OrderDTO>,
            ) {
                Log.d("OrderRemoteRepository", response.body().toString())
                response.body()?.let {
                    callback(it)
                }
            }

            override fun onFailure(call: Call<OrderDTO>, t: Throwable) {
                throw t
            }
        })
    }

    override fun order(cartProducts: OrderCartItemsDTO, callback: (Int?) -> Unit) {
        retrofitService.requestOrderCartItems(cartProducts).enqueue(object : retrofit2.Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                Log.d("orderId", response.headers().toString())
                callback(response.headers()["Location"]?.substringAfterLast("/")?.toInt())
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                throw t
            }
        })
    }
}
