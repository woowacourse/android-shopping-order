package woowacourse.shopping.data.service.retrofit.order

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.dto.OrderRequest
import woowacourse.shopping.data.dto.OrderResponse
import woowacourse.shopping.domain.model.OrderItems

interface RetrofitOrderService {
    @POST("/orders")
    fun orderProducts(
        @Header("Authorization") token: String,
        @Body orderItems: OrderItems,
    ): Call<OrderRequest>

    @GET("/orders")
    fun requestOrders(
        @Header("Authorization") token: String,
    ): Call<List<OrderResponse>>

    @GET("/orders/{orderId}")
    fun requestSpecificOrder(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: String,
    ): Call<OrderResponse>
}
