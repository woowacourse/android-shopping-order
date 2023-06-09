package woowacourse.shopping.data.order

import com.example.domain.order.Order
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.order.model.dto.request.OrderRequest
import woowacourse.shopping.data.order.model.dto.response.FixedDiscountPolicyResponse
import woowacourse.shopping.data.order.model.dto.response.OrderSummaryResponse

interface RetrofitOrderService {

    @GET("/orders")
    fun requestAllOrders(): Call<List<OrderSummaryResponse>>

    @GET("/orders/{id}")
    fun requestFetchOrderById(
        @Path("id") id: Long
    ): Call<Order>

    @POST("/orders")
    fun requestAddOrder(
        @Body request: OrderRequest
    ): Call<Unit>

    @GET("/orders/discount-policies")
    fun requestFetchDiscountPolicy(): Call<FixedDiscountPolicyResponse>
}
