package woowacourse.shopping.data.order

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.entity.PayRequest
import woowacourse.shopping.data.entity.PayResponse
import woowacourse.shopping.data.server.OrderRemoteDataSource

class DefaultOrderRemoteDataSource(private val service: OrderService) : OrderRemoteDataSource {
    override fun addOrder(order: PayRequest, onSuccess: (Int) -> Unit, onFailure: (String) -> Unit) {
        service.requestOrder(order).enqueue(object : Callback<PayResponse> {
            override fun onResponse(call: Call<PayResponse>, response: Response<PayResponse>) {
                if(response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!.orderId)
                }
                else {
                    onFailure(response.message().ifBlank { MESSAGE_ORDER_FAILED })
                }
            }

            override fun onFailure(call: Call<PayResponse>, t: Throwable) {
                onFailure(MESSAGE_ORDER_FAILED)
            }
        })
    }

    companion object {
        private const val MESSAGE_ORDER_FAILED = "상품을 주문하는데 실패했습니다."
    }
}