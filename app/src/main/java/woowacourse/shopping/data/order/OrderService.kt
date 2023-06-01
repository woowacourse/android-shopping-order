package woowacourse.shopping.data.order

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import woowacourse.shopping.data.order.requestbody.OrderRequestBody

interface OrderService {
    @Headers("Content-Type: application/json")
    @POST("orders")
    fun order(
        @Header("Authorization") credentials: String,
        @Body orderRequestBody: OrderRequestBody,
    ): Call<Unit>
}
