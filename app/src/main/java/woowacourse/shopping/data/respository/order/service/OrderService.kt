package woowacourse.shopping.data.respository.order.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import woowacourse.shopping.data.model.OrderPostEntity

interface OrderService {
    @POST("/orders")
    fun requestPostData(
        @Header("Authorization")
        token: String,
        @Body
        order: OrderPostEntity,
    ): Call<Unit>
}
