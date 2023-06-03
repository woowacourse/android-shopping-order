package woowacourse.shopping.data.datasource.remote.order

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.model.OrderDetailDto
import woowacourse.shopping.data.model.OrderListDto
import woowacourse.shopping.data.model.OrderRequestDto

interface OrderService {
    @GET("/orders")
    fun getAll(@Query("page") page: Int): Call<List<OrderListDto>>

    @GET("/orders/{orderId}")
    fun getOrderDetail(@Path("orderId") orderId: Int): Call<OrderDetailDto>

    @DELETE("/orders/{orderId}")
    fun deleteOrder(@Path("orderId") orderId: Int): Call<Unit>

    @POST("/orders")
    fun placeOrder(@Body orderRequest: OrderRequestDto)
}
