package woowacourse.shopping.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import woowacourse.shopping.data.remote.dto.cart.CartOrderRequest
import woowacourse.shopping.data.remote.dto.order.CouponDto

interface OrderApiService {
    @POST("/orders")
    suspend fun orderItems(
        @Header("accept") accept: String = "*/*",
        @Body cartItemIds: CartOrderRequest,
    ): Response<Unit>

    @GET("/coupons")
    suspend fun getCoupons(
        @Header("accept") accept: String = "*/*",
    ): Response<List<CouponDto>>
}
