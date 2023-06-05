package woowacourse.shopping.data.respository.order.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.model.dto.request.OrderRequest
import woowacourse.shopping.data.model.dto.response.OrderDetailResponse

interface OrderService {
    @POST("/orders")
    fun requestPostData(
        @Body
        order: OrderRequest,
    ): Call<Unit>

    @GET("/orders/{orderId}")
    fun requestOrderItem(
        @Path("orderId")
        orderId: Long,
    ): Call<OrderDetailResponse>

    @GET("/orders")
    fun requestOrderList(): Call<List<OrderDetailResponse>>
}
