package woowacourse.shopping.data.remote.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import woowacourse.shopping.data.remote.request.OrderWithCouponRequestDto
import woowacourse.shopping.data.remote.request.OrderWithoutCouponRequestDto
import woowacourse.shopping.data.remote.response.AppliedTotalResponseDto
import woowacourse.shopping.data.remote.response.CouponsResponseDto
import woowacourse.shopping.data.remote.response.OrderCompleteResponseDto

interface OrderService {

    @GET("/coupons")
    fun getCoupons(
        @Header("Authorization") token: String,
    ): Call<List<CouponsResponseDto>>

    @GET("/coupons/discount")
    fun getAppliedPrice(
        @Header("Authorization") token: String,
        @Query("origin-price") originPrice: Int,
        @Query("member-coupon-id") couponId: Int,
    ): Call<AppliedTotalResponseDto>

    @POST("/orders")
    fun postOrderWithCoupon(
        @Header("Authorization") token: String,
        @Body orderWithCoupon: OrderWithCouponRequestDto,
    ): Call<OrderCompleteResponseDto>

    @POST("/orders")
    fun postOrderWithoutCoupon(
        @Header("Authorization") token: String,
        @Body orderWithCoupon: OrderWithoutCouponRequestDto,
    ): Call<OrderCompleteResponseDto>
}
