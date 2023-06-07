package woowacourse.shopping.data.order

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.data.order.request.PostOrderRequest
import woowacourse.shopping.data.order.response.PostOrderResponse

interface OrderService {
    @POST("pay")
    fun requestOrder(
        @Body body: PostOrderRequest
    ) : Call<PostOrderResponse>
}