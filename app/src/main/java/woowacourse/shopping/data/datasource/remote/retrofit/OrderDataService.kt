package woowacourse.shopping.data.datasource.remote.retrofit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import woowacourse.shopping.data.remote.request.OrderDTO
import woowacourse.shopping.data.remote.request.OrderRequestWithoutCoupon

interface OrderDataService {

    @POST("orders")
    fun postOrderWithCoupon(
        @Header("Authorization") token: String,
        @Body cartItemsIds: List<Long>,
        @Body memberCouponId: Long,
    ): Call<OrderDTO>

    @POST("orders")
    fun postOrderWithoutCoupon(
        @Header("Authorization") token: String,
        @Body orderRequestWithoutCoupon: OrderRequestWithoutCoupon,
    ): Call<OrderDTO>
}
