package woowacourse.shopping.data.coupon.service

import retrofit2.http.GET
import woowacourse.shopping.data.coupon.dto.CouponResponse

interface CouponService {
    @GET("/coupons")
    fun getCoupons(
    ): CouponResponse?
}