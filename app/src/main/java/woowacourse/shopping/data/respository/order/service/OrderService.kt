package woowacourse.shopping.data.respository.order.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.model.OrderDetailEntity
import woowacourse.shopping.data.model.OrderPostEntity

interface OrderService {
    @POST("/orders")
    fun requestPostData(
        @Header("Authorization")
        token: String,
        @Body
        order: OrderPostEntity,
    ): Call<Unit>

    @GET("/orders/{orderId}")
    fun requestOrderItem(
        @Header("Authorization")
        token: String,
        @Path("orderId")
        orderId: Long,
    ): Call<OrderDetailEntity>
}
