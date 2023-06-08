package woowacourse.shopping.data.service.order

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.dto.OrderRequest
import woowacourse.shopping.data.dto.OrderResponse
import woowacourse.shopping.data.dto.OrderResponsesDto

interface RetrofitOrderService {
    @POST("/orders")
    fun orderProducts(
        @Header("Authorization") token: String,
        @Body orderRequest: OrderRequest,
    ): Call<Unit>

    @GET("/orders")
    fun requestOrders(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<OrderResponsesDto>

    @GET("/orders/{orderId}")
    fun requestSpecificOrder(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: String,
    ): Call<OrderResponse>
}
