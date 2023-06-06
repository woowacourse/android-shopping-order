package woowacourse.shopping.data.remote.order

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.common.BaseResponse
import woowacourse.shopping.data.remote.order.requestbody.OrderRequestBody
import woowacourse.shopping.data.remote.order.response.OrderDataModel
import woowacourse.shopping.data.remote.order.response.OrderDetailDataModel

interface OrderService {
    @POST("orders")
    fun order(
        @Body orderRequestBody: OrderRequestBody,
    ): Call<Unit>

    @GET("orders")
    fun loadOrderList(): Call<BaseResponse<List<OrderDataModel>>>

    @GET("orders/{orderId}")
    fun loadOrderDetail(
        @Path("orderId") orderId: Int
    ): Call<BaseResponse<OrderDetailDataModel>>
}
