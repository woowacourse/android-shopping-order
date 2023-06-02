package woowacourse.shopping.data.datasource.order

import android.util.Log
import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.data.NetworkModule
import woowacourse.shopping.data.datasource.request.OrderRequest
import woowacourse.shopping.data.datasource.response.OrderResponse
import woowacourse.shopping.data.remote.OkHttpModule

class OrderRemoteDataSourceImpl : OrderRemoteDataSource {

    private val orderService: OrderService = NetworkModule.getService()

    override fun addOrder(
        orderRequest: OrderRequest,
        onAdded: (orderId: Long) -> Unit,
        onFailed: (errorMessage: String) -> Unit,
    ) {
        orderService.addOrder(
            authorization = OkHttpModule.AUTHORIZATION_FORMAT.format(OkHttpModule.encodedUserInfo),
            orderRequest = orderRequest
        ).enqueue(object : retrofit2.Callback<OrderResponse> {

            override fun onResponse(
                call: Call<OrderResponse>,
                response: Response<OrderResponse>,
            ) {
                if (response.code() == 409) {
                    onFailed(STOCK_ERROR)
                } else {
                    response.headers()[LOCATION]?.let {
                        val orderId = it.split("/")
                            .last()
                            .toLong()

                        onAdded(orderId)
                    }
                }
            }

            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                onFailed(RESPONSE_ERROR)
            }
        })
    }

    override fun getOrder(
        orderId: Int,
        onReceived: (OrderResponse) -> Unit,
    ) {
        orderService.getOrder(
            authorization = OkHttpModule.AUTHORIZATION_FORMAT.format(OkHttpModule.encodedUserInfo),
            orderId = orderId
        ).enqueue(object : retrofit2.Callback<OrderResponse> {

            override fun onResponse(
                call: Call<OrderResponse>,
                response: Response<OrderResponse>,
            ) {
                response.body()?.let {
                    onReceived(it)
                } ?: onFailed(ORDER_INFO_ERROR)
            }

            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                onFailed(ORDER_INFO_ERROR)
            }
        })
    }

    override fun getOrders(onReceived: (List<OrderResponse>) -> Unit) {
        orderService.getOrders(
            authorization = OkHttpModule.AUTHORIZATION_FORMAT.format(OkHttpModule.encodedUserInfo)
        ).enqueue(object : retrofit2.Callback<List<OrderResponse>> {

            override fun onResponse(
                call: Call<List<OrderResponse>>,
                response: Response<List<OrderResponse>>,
            ) {
                Log.d("woogi", "주문목록 조회: ${response.body()}")
                response.body()?.let {
                    onReceived(it)
                }
            }

            override fun onFailure(call: Call<List<OrderResponse>>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    companion object {
        private const val RESPONSE_ERROR = "서버로부터 응답을 받지 못했습니다."
        private const val STOCK_ERROR = "상품 재고가 부족해 주문에 실패했습니다."
        private const val LOCATION = "Location"
    }
}
