package woowacourse.shopping.data.remote.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.data.remote.request.AddOrderRequest

interface OrderService {
    @POST("/orders")
    fun addOrder(
        @Body body: AddOrderRequest
    ): Call<Unit>
}
