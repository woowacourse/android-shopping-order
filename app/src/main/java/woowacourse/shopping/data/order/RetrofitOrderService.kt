package woowacourse.shopping.data.order

import com.example.domain.order.Order
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.order.model.dto.request.OrderRequest
import woowacourse.shopping.data.order.model.dto.response.FixedDiscountPoliciesResponse
import woowacourse.shopping.data.order.model.dto.response.OrderSummaryResponse

interface RetrofitOrderService {

    @GET("/orders")
    fun requestAllOrders(): Call<List<OrderSummaryResponse>>

    // todo 서버 배포 완료 후 검증 필요
    @GET("/orders/{id}")
    fun requestFetchOrderById(
        @Path("id") id: Long
    ): Call<Order>

    // todo 서버 배포 완료 후 검증 필요
    @POST("/orders")
    fun requestAddOrder(
        @Body request: OrderRequest
    ): Call<Unit>

    @POST("/orders/discount-policies")
    fun requestFetchDiscountPolicy(): Call<FixedDiscountPoliciesResponse>
}
