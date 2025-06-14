package woowacourse.shopping.data.coupon.service

import retrofit2.http.GET
import woowacourse.shopping.data.coupon.dto.CouponResponseItem

interface CouponService {
    @GET("/coupons")
    suspend fun getCoupons(
    ): List<CouponResponseItem>
}