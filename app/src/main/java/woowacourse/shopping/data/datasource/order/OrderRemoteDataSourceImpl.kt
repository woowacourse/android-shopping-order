package woowacourse.shopping.data.datasource.order

import android.util.Log
import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.data.NetworkModule
import woowacourse.shopping.data.model.DataOrder
import woowacourse.shopping.data.model.OrderRequest
import woowacourse.shopping.data.remote.OkHttpModule

class OrderRemoteDataSourceImpl : OrderRemoteDataSource {

    private val orderService: OrderService = NetworkModule.getService()

    override fun addOrder(
        orderRequest: OrderRequest,
        onAdded: (orderId: Long) -> Unit,
    ) {
        orderService.addOrder(
            authorization = OkHttpModule.AUTHORIZATION_FORMAT.format(OkHttpModule.encodedUserInfo),
            orderRequest = orderRequest
        ).enqueue(object : retrofit2.Callback<DataOrder> {

            override fun onResponse(
                call: Call<DataOrder>,
                response: Response<DataOrder>,
            ) {
                response.headers()["Location"]?.let {
                    val orderId = it.split("/").last().toLong()

                    onAdded(orderId)
                }
            }

            override fun onFailure(call: Call<DataOrder>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun getOrder(
        orderId: Int,
        onReceived: (DataOrder) -> Unit,
    ) {
        orderService.getOrder(
            authorization = OkHttpModule.AUTHORIZATION_FORMAT.format(OkHttpModule.encodedUserInfo),
            orderId = orderId
        ).enqueue(object : retrofit2.Callback<DataOrder> {

            override fun onResponse(
                call: Call<DataOrder>,
                response: Response<DataOrder>,
            ) {
                Log.d("woogi", "onResponse: ${response.body()}")
                response.body()?.let {
                    onReceived(it)
                }
            }

            override fun onFailure(call: Call<DataOrder>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun getOrders(onReceived: (List<DataOrder>) -> Unit) {
        orderService.getOrders(
            authorization = OkHttpModule.AUTHORIZATION_FORMAT.format(OkHttpModule.encodedUserInfo)
        ).enqueue(object : retrofit2.Callback<List<DataOrder>> {

            override fun onResponse(
                call: Call<List<DataOrder>>,
                response: Response<List<DataOrder>>,
            ) {
                Log.d("woogi", "onResponse: ${response.body()}")
                response.body()?.let {
                    onReceived(it)
                }
            }

            override fun onFailure(call: Call<List<DataOrder>>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
}
