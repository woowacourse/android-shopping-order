package woowacourse.shopping.data.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.dto.OrderHistoryDto
import woowacourse.shopping.dto.OrderHistoryResponseDto
import woowacourse.shopping.dto.PostOrderRequestDto
import woowacourse.shopping.model.Order

interface RetrofitOrderService {
    @GET("cart-items/checkout")
    fun getOrderList(
        @Query("ids") cartItemIds: String
    ): Call<Order>

    @GET("orders")
    fun getOrders(): Call<OrderHistoryResponseDto>

    @GET("orders")
    fun getOrdersNext(
        @Query("last-id") lastOrderId: Long
    ): Call<OrderHistoryResponseDto>

    @GET("orders/{id}")
    fun getOrder(
        @Path("id") id: Long
    ): Call<OrderHistoryDto>

    @POST("orders")
    fun postOrder(
        @Body request: PostOrderRequestDto
    ): Call<Unit>
}
