package woowacourse.shopping.data.datasource.remote.orderhistory

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.datasource.remote.retrofit.RetrofitClient
import woowacourse.shopping.data.remote.request.OrderDTO

class OrderHistoryRemoteSourceImpl :
    OrderHistoryRemoteSource {

    override fun getOrderList(callback: (Result<List<OrderDTO>>) -> Unit) {
        RetrofitClient.getInstance().orderDataService.getOrderList().enqueue(
            object : Callback<List<OrderDTO>> {
                override fun onResponse(
                    call: Call<List<OrderDTO>>,
                    response: Response<List<OrderDTO>>,
                ) {
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

                override fun onFailure(call: Call<List<OrderDTO>>, t: Throwable) {
                    throw t
                }
            },
        )
    }
}
