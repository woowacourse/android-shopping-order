package woowacourse.shopping.data.coupon.remote.service

import retrofit2.http.GET
import woowacourse.shopping.data.coupon.remote.dto.CouponResponseDto

interface CouponService {
    @GET("coupons")
    suspend fun getCoupons(): List<CouponResponseDto>
}
