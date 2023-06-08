package woowacourse.shopping.data.remote

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.remote.dto.OrderCartItemsDTO
import woowacourse.shopping.data.remote.dto.OrdersDTO

interface OrderApi {
    @GET("/orders")
    fun requestOrders(): Call<OrdersDTO>

    @GET("/orders/{id}")
    fun requestOrderDetail(@Path("id") orderId: Int): Call<OrdersDTO.OrderDTO>

    @POST("/orders")
    fun requestOrderCartItems(@Body orderCartItemDtos: OrderCartItemsDTO): Call<Unit>
}
