package woowacourse.shopping.data.order

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.order.dto.Order
import woowacourse.shopping.data.order.dto.OrderCartItemDtos
import woowacourse.shopping.data.order.dto.Orders

interface OrderRetrofitService {
    @GET("/orders")
    fun loadOrders(
        @Header("Authorization") authorization: String,
    ): Call<Orders>

    @GET("/orders/{orderId}")
    fun loadOrder(
        @Header("Authorization") authorization: String,
        @Path("orderId") orderId: Long,
    ): Call<Order>

    @POST("/orders")
    fun orderCartItems(
        @Header("Authorization") authorization: String,
        @Body orderCartItemDtos: OrderCartItemDtos,
    ): Call<Unit>
}
