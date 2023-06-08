package woowacourse.shopping.data.order

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.order.dto.Order
import woowacourse.shopping.data.order.dto.OrderCartItemDtos
import woowacourse.shopping.data.order.dto.Orders

interface OrderRetrofitService {
    @GET("/orders")
    fun loadOrders(): Call<Orders>

    @GET("/orders/{orderId}")
    fun loadOrder(
        @Path("orderId") orderId: Long,
    ): Call<Order>

    @POST("/orders")
    fun orderCartItems(
        @Body orderCartItemDtos: OrderCartItemDtos,
    ): Call<Unit>
}
