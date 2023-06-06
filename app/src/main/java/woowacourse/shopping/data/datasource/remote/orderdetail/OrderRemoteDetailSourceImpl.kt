package woowacourse.shopping.data.datasource.remote.orderdetail

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.datasource.remote.retrofit.RetrofitClient
import woowacourse.shopping.data.remote.request.OrderDTO

class OrderRemoteDetailSourceImpl :
    OrderRemoteDetailSource {

    override fun getById(orderId: Long, callback: (Result<OrderDTO>) -> Unit) {
        RetrofitClient.getInstance().orderDataService.getById(orderId).enqueue(
            object : Callback<OrderDTO> {
                override fun onResponse(call: Call<OrderDTO>, response: Response<OrderDTO>) {
                    if (response.isSuccessful) {
                        callback(
                            Result.success(
                                response.body() ?: throw IllegalArgumentException(),
                            ),
                        )
                    } else {
                        callback(Result.failure(Throwable(response.message())))
                    }
                }

                override fun onFailure(call: Call<OrderDTO>, t: Throwable) {
                    throw t
                }
            },
        )
    }
}
