package woowacourse.shopping.data.order

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.common.BaseResponse
import woowacourse.shopping.data.order.requestbody.OrderRequestBody
import woowacourse.shopping.data.order.response.OrderDataModel
import woowacourse.shopping.data.order.response.OrderDetailDataModel

interface OrderService {
    @Headers("Content-Type: application/json")
    @POST("orders")
    fun order(
        @Header("Authorization") credentials: String,
        @Body orderRequestBody: OrderRequestBody,
    ): Call<Unit>

    @GET("orders")
    fun loadOrderList(
        @Header("Authorization") credentials: String
    ): Call<BaseResponse<List<OrderDataModel>>>

    @GET("orders/{orderId}")
    fun loadOrderDetail(
        @Header("Authorization") credentials: String,
        @Path("orderId") orderId: Int
    ): Call<BaseResponse<OrderDetailDataModel>>
}
