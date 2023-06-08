package woowacourse.shopping.data.order

import android.os.Handler
import android.os.Looper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.order.request.PostOrderRequest
import woowacourse.shopping.data.order.response.PostOrderResponse
import woowacourse.shopping.data.server.OrderRemoteDataSource

class DefaultOrderRemoteDataSource(private val service: OrderService) : OrderRemoteDataSource {
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun addOrder(order: PostOrderRequest, onSuccess: (Int) -> Unit, onFailure: (String) -> Unit) {
        service.requestOrder(order).enqueue(object : Callback<PostOrderResponse> {
            override fun onResponse(call: Call<PostOrderResponse>, response: Response<PostOrderResponse>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null) {
                    postToMainHandler { onSuccess(responseBody.orderId) }
                }
                else {
                    postToMainHandler { onFailure(response.message().ifBlank { MESSAGE_ORDER_FAILED }) }
                }
            }

            override fun onFailure(call: Call<PostOrderResponse>, t: Throwable) {
                postToMainHandler { onFailure(MESSAGE_ORDER_FAILED) }
            }
        })
    }

    private fun postToMainHandler(block: () -> Unit) {
        mainHandler.post {
            block()
        }
    }

    companion object {
        private const val MESSAGE_ORDER_FAILED = "상품을 주문하는데 실패했습니다."
    }
}