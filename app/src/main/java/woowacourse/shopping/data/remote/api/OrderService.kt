package woowacourse.shopping.data.remote.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import woowacourse.shopping.data.remote.request.OrderWithCouponRequestDto
import woowacourse.shopping.data.remote.request.OrderWithoutCouponRequestDto
import woowacourse.shopping.data.remote.response.AppliedTotalResponseDto
import woowacourse.shopping.data.remote.response.CouponsResponseDto
import woowacourse.shopping.data.remote.response.OrderCompleteResponseDto

interface OrderService {

    @GET("/coupons")
    fun getCoupons(): Call<List<CouponsResponseDto>>

    @GET("/coupons/discount")
    fun getAppliedPrice(
        @Query("origin-price") originPrice: Int,
        @Query("member-coupon-id") couponId: Int,
    ): Call<AppliedTotalResponseDto>

    @POST("/orders")
    fun postOrderWithCoupon(
        @Body orderWithCoupon: OrderWithCouponRequestDto,
    ): Call<OrderCompleteResponseDto>

    @POST("/orders")
    fun postOrderWithoutCoupon(
        @Body orderWithCoupon: OrderWithoutCouponRequestDto,
    ): Call<OrderCompleteResponseDto>
}
