package woowacourse.shopping.data.remote

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.data.dto.OrderRequest

interface OrderService {
    @POST("/orders")
    fun postOrder(
        @Body orderRequest: OrderRequest,
    ): Call<Unit>
}
