package woowacourse.shopping.data.datasource.remote.retrofit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import woowacourse.shopping.data.remote.request.OrderDTO
import woowacourse.shopping.data.remote.request.OrderRequestWithCoupon
import woowacourse.shopping.data.remote.request.OrderRequestWithoutCoupon

interface OrderDataService {

    @POST("orders")
    fun postOrderWithCoupon(
        @Body orderRequestWithCoupon: OrderRequestWithCoupon,
    ): Call<OrderDTO>

    @POST("orders")
    fun postOrderWithoutCoupon(
        @Body orderRequestWithoutCoupon: OrderRequestWithoutCoupon,
    ): Call<OrderDTO>

    @GET("orders")
    fun getOrderList(): Call<List<OrderDTO>>

    @GET("orders/{orderId}")
    fun getById(
        @Path("orderId") orderId: Long,
    ): Call<OrderDTO>
}
