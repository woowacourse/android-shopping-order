package woowacourse.shopping.data.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.model.Order
import woowacourse.shopping.model.OrderHistory
import woowacourse.shopping.model.OrderHistoryResponse
import woowacourse.shopping.model.PostOrderRequest

interface RetrofitOrderService {
    @GET("cart-items/checkout")
    fun getOrderList(
        @Query("ids") cartItemIds: String
    ): Call<Order>

    @GET("orders")
    fun getOrders(): Call<OrderHistoryResponse>

    @GET("orders")
    fun getOrdersNext(
        @Query("last-id") lastOrderId: Long
    ): Call<OrderHistoryResponse>

    @GET("orders/{id}")
    fun getOrder(
        @Path("id") id: Long
    ): Call<OrderHistory>

    @POST("orders")
    fun postOrder(
        @Body request: PostOrderRequest
    ): Call<Unit>
}
