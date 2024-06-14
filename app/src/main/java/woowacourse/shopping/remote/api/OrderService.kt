package woowacourse.shopping.remote.api

import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.remote.model.request.PostOrderRequest

interface OrderService {
    @POST(ORDER_RELATIVE_URL)
    suspend fun postOrder(
        @Body body: PostOrderRequest,
    )

    companion object {
        private const val ORDER_RELATIVE_URL = "/orders"
    }
}
