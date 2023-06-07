package woowacourse.shopping.data.order

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.order.request.PostOrderRequest
import woowacourse.shopping.data.order.response.PostOrderResponse
import woowacourse.shopping.data.server.OrderRemoteDataSource

class DefaultOrderRemoteDataSource(private val service: OrderService) : OrderRemoteDataSource {
    override fun addOrder(order: PostOrderRequest, onSuccess: (Int) -> Unit, onFailure: (String) -> Unit) {
        service.requestOrder(order).enqueue(object : Callback<PostOrderResponse> {
            override fun onResponse(call: Call<PostOrderResponse>, response: Response<PostOrderResponse>) {
                if(response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!.orderId)
                }
                else {
                    onFailure(response.message().ifBlank { MESSAGE_ORDER_FAILED })
                }
            }

            override fun onFailure(call: Call<PostOrderResponse>, t: Throwable) {
                onFailure(MESSAGE_ORDER_FAILED)
            }
        })
    }

    companion object {
        private const val MESSAGE_ORDER_FAILED = "상품을 주문하는데 실패했습니다."
    }
}