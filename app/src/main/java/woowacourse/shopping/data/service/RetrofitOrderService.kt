package woowacourse.shopping.data.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import woowacourse.shopping.data.model.OrderListResponse
import woowacourse.shopping.data.model.OrderRequest
import woowacourse.shopping.model.OrderInfo

interface RetrofitOrderService {
    @GET("cart-items/checkout")
    fun orderCart(
        @Query("ids") itemIds: List<Int>,
    ): Call<OrderInfo>

    @POST("orders")
    fun postOrderItem(
        @Body orderRequest: OrderRequest,
    ): Call<Unit>

    @GET("orders")
    fun getOrders(): Call<OrderListResponse>
}
