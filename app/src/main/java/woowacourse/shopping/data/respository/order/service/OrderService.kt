package woowacourse.shopping.data.respository.order.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.model.OrderDetailEntity
import woowacourse.shopping.data.model.OrderPostEntity

interface OrderService {
    @POST("/orders")
    fun requestPostData(
        @Body
        order: OrderPostEntity,
    ): Call<Unit>

    @GET("/orders/{orderId}")
    fun requestOrderItem(
        @Path("orderId")
        orderId: Long,
    ): Call<OrderDetailEntity>

    @GET("/orders")
    fun requestOrderList(): Call<List<OrderDetailEntity>>
}
