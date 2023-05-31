package woowacourse.shopping.data.order

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.common.model.BaseResponse
import woowacourse.shopping.data.order.model.OrderDataModel
import woowacourse.shopping.data.order.model.OrderDetailDataModel
import woowacourse.shopping.data.order.model.OrderItemBody

interface OrderService {
    @GET("orders")
    fun getOrders(
        @Header("Authorization") credentials: String,
    ): Call<BaseResponse<List<OrderDataModel>>>

    @GET("orders/{orderId}")
    fun getOrderDetail(
        @Header("Authorization") credentials: String,
        @Path("orderId") orderId: Int,
    ): Call<BaseResponse<OrderDetailDataModel>>

    @POST("orders")
    fun addOrder(
        @Header("Authorization") credentials: String,
        @Body orderItemBodies: List<OrderItemBody>,
    ): Call<BaseResponse<Unit>>
}
