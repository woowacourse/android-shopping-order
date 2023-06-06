package woowacourse.shopping.data.datasource.remote.order

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.datasource.remote.retrofit.RetrofitClient
import woowacourse.shopping.data.remote.request.OrderDTO
import woowacourse.shopping.data.remote.request.OrderRequestWithCoupon
import woowacourse.shopping.data.remote.request.OrderRequestWithoutCoupon

class OrderRemoteDataSourceImpl : OrderRemoteDataSource {

    override fun postOrderWithCoupon(cartItemsIds: List<Long>, couponId: Long, callback: (Result<OrderDTO>) -> Unit) {
        RetrofitClient.getInstance().orderDataService.postOrderWithCoupon(
            OrderRequestWithCoupon(cartItemsIds, couponId),
        ).enqueue(object : Callback<OrderDTO> {
            override fun onResponse(call: Call<OrderDTO>, response: Response<OrderDTO>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body() ?: throw IllegalArgumentException()))
                } else {
                    callback(Result.failure(Throwable(response.message())))
                }
            }

            override fun onFailure(call: Call<OrderDTO>, t: Throwable) {
                throw t
            }
        })
    }

    override fun postOrderWithoutCoupon(cartItemsIds: List<Long>, callback: (Result<OrderDTO>) -> Unit) {
        RetrofitClient.getInstance().orderDataService.postOrderWithoutCoupon(
            OrderRequestWithoutCoupon(cartItemsIds),
        ).enqueue(object : Callback<OrderDTO> {
            override fun onResponse(call: Call<OrderDTO>, response: Response<OrderDTO>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body() ?: throw IllegalArgumentException()))
                } else {
                    callback(Result.failure(Throwable(response.message())))
                }
            }

            override fun onFailure(call: Call<OrderDTO>, t: Throwable) {
                throw t
            }
        })
    }
}
