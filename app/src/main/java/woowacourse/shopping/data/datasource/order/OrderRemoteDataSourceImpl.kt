package woowacourse.shopping.data.datasource.order

import android.util.Log
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.NullOnEmptyConvertFactory
import woowacourse.shopping.data.model.DataOrderRecord
import woowacourse.shopping.data.model.OrderRequest
import woowacourse.shopping.data.remote.OkHttpModule

class OrderRemoteDataSourceImpl : OrderRemoteDataSource {

    private val url = OkHttpModule.BASE_URL
    private val orderService = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(NullOnEmptyConvertFactory)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OrderService::class.java)

    override fun addOrder(
        orderRequest: OrderRequest,
        onAdded: (orderId: Long) -> Unit,
    ) {
        orderService.addOrder(
            authorization = OkHttpModule.AUTHORIZATION_FORMAT.format(OkHttpModule.encodedUserInfo),
            orderRequest = orderRequest
        ).enqueue(object : retrofit2.Callback<DataOrderRecord> {

            override fun onResponse(
                call: Call<DataOrderRecord>,
                response: Response<DataOrderRecord>,
            ) {
                response.headers()["Location"]?.let {
                    val orderId = it.split("/").last().toLong()

                    onAdded(orderId)
                }
            }

            override fun onFailure(call: Call<DataOrderRecord>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun getOrderRecord(
        orderId: Int,
        onReceived: (DataOrderRecord) -> Unit,
    ) {
        orderService.getOrderRecord(
            authorization = OkHttpModule.AUTHORIZATION_FORMAT.format(OkHttpModule.encodedUserInfo),
            orderId = orderId
        ).enqueue(object : retrofit2.Callback<DataOrderRecord> {

            override fun onResponse(
                call: Call<DataOrderRecord>,
                response: Response<DataOrderRecord>,
            ) {
                Log.d("woogi", "onResponse: ${response.body()}")
                response.body()?.let {
                    onReceived(it)
                }
            }

            override fun onFailure(call: Call<DataOrderRecord>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
}
