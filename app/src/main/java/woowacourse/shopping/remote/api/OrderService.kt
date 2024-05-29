package woowacourse.shopping.remote.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.remote.model.request.PostOrderRequest

interface OrderService {
    @POST(ApiClient.Order.POST_ORDER)
    fun postOrder(
        @Body body: PostOrderRequest,
    ): Call<Unit>
}
