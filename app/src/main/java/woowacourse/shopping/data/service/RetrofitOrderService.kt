package woowacourse.shopping.data.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.dto.OrderHistoryDto
import woowacourse.shopping.data.dto.OrderInfoDto
import woowacourse.shopping.data.dto.OrderListResponse
import woowacourse.shopping.data.dto.OrderRequest

interface RetrofitOrderService {
    @GET("cart-items/checkout")
    fun orderCart(
        @Query("ids") itemIds: List<Int>,
    ): Call<OrderInfoDto>

    @POST("orders")
    fun postOrderItem(
        @Body orderRequest: OrderRequest,
    ): Call<Unit>

    @GET("orders")
    fun getOrders(): Call<OrderListResponse>

    @GET("orders/{id}")
    fun getOrder(
        @Path("id") id: Int,
    ): Call<OrderHistoryDto>
}
