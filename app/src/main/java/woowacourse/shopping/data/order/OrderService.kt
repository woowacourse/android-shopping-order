package woowacourse.shopping.data.order

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import woowacourse.shopping.data.entity.OrderRequest
import woowacourse.shopping.data.entity.OrderResponse

interface OrderService {
    @POST("pay")
    fun requestOrder(
        @Header("Authorization") authorization: String,
        @Body body: OrderRequest
    ) : Call<OrderResponse>
}