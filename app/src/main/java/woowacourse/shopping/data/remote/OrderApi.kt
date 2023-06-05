package woowacourse.shopping.data.remote

import retrofit2.Call
import retrofit2.http.*
import woowacourse.shopping.data.remote.dto.OrderCartItemsDTO
import woowacourse.shopping.data.remote.dto.OrderDTO
import woowacourse.shopping.data.remote.dto.OrdersDTO

interface OrderApi {
    @GET("/orders")
    fun requestOrders(): Call<OrdersDTO>

    @GET("/orders/{id}")
    fun requestOrderDetail(@Path("id") orderId: Int): Call<OrderDTO>

    @POST("/orders")
    fun requestOrderCartItems(@Body orderCartItemDtos: OrderCartItemsDTO): Call<Unit>
}
