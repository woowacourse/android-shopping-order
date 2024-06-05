package woowacourse.shopping.data.order

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.data.dto.request.OrderRequest

interface OrderApiService {
    @POST("/orders")
    fun requestOrder(
        @Body orderRequest: OrderRequest,
    ): Call<Unit>
}
