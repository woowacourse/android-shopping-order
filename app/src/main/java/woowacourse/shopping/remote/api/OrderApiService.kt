package woowacourse.shopping.remote.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.remote.dto.request.OrderRequest

interface OrderApiService {
    @POST("/orders")
    fun requestOrder(
        @Body orderRequest: OrderRequest,
    ): Call<Unit>
}
