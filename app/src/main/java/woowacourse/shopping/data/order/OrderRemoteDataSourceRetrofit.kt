package woowacourse.shopping.data.order

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import woowacourse.shopping.Storage
import woowacourse.shopping.data.entity.PayRequest
import woowacourse.shopping.data.entity.PayResponse
import woowacourse.shopping.data.server.OrderRemoteDataSource
import woowacourse.shopping.data.server.Server

class OrderRemoteDataSourceRetrofit : OrderRemoteDataSource {
    private val orderService: OrderService = Retrofit.Builder()
        .baseUrl(Server.getUrl(Storage.server))
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(OrderService::class.java)

    override fun addOrder(order: PayRequest, onSuccess: (Int) -> Unit, onFailure: () -> Unit) {
        orderService.requestOrder(Storage.credential, order).enqueue(object : Callback<PayResponse> {
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