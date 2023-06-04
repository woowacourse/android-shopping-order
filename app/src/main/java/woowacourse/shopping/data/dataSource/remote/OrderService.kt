package woowacourse.shopping.data.dataSource.remote

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.model.dto.request.OrderRequestDto
import woowacourse.shopping.data.model.dto.response.OrderDetailDto
import woowacourse.shopping.data.model.dto.response.OrderMinInfoItemDto

interface OrderService {
    @POST("orders")
    fun addOrder(
        @Body orderRequestDto: OrderRequestDto
    ): Call<Unit>

    @GET("orders")
    fun getOrders(): Call<List<OrderMinInfoItemDto>>

    @GET("orders/{orderId}")
    fun getOrderDetail(
        @Path("orderId") orderId: Long
    ): Call<OrderDetailDto>
}
