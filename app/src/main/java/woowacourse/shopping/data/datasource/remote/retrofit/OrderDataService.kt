package woowacourse.shopping.data.datasource.remote.retrofit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.remote.request.OrderDTO
import woowacourse.shopping.data.remote.request.OrderRequestWithCoupon
import woowacourse.shopping.data.remote.request.OrderRequestWithoutCoupon

interface OrderDataService {

    @POST("orders")
    fun postOrderWithCoupon(
        @Header("Authorization") token: String,
        @Body orderRequestWithCoupon: OrderRequestWithCoupon,
    ): Call<OrderDTO>

    @POST("orders")
    fun postOrderWithoutCoupon(
        @Header("Authorization") token: String,
        @Body orderRequestWithoutCoupon: OrderRequestWithoutCoupon,
    ): Call<OrderDTO>

    @GET("orders")
    fun getOrderList(
        @Header("Authorization") token: String,
    ): Call<List<OrderDTO>>

    @GET("orders/{orderId}")
    fun getById(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: Long,
    ): Call<OrderDTO>
}
