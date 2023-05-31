package woowacourse.shopping.data.order

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.common.model.BaseResponse
import woowacourse.shopping.data.order.model.OrderDataModel
import woowacourse.shopping.data.order.model.OrderDetailDataModel
import woowacourse.shopping.data.order.model.OrderItemBody

interface OrderService {
    @GET("orders")
    fun getOrders(): Call<BaseResponse<List<OrderDataModel>>>

    @GET("orders/{orderId}")
    fun getOrderDetail(
        @Path("orderId") orderId: Int,
    ): Call<BaseResponse<OrderDetailDataModel>>

    @POST("orders")
    fun addOrder(
        @Body orderItemsBody: List<OrderItemBody>,
    ): Call<BaseResponse<Unit>>
}
