package woowacourse.shopping.data.service

import retrofit2.http.GET
import woowacourse.shopping.data.dto.response.coupon.CouponResponseDto

interface CouponApiService {
    @GET("/coupons")
    suspend fun getCoupons(): Result<List<CouponResponseDto>>
}
