package woowacourse.shopping.data.remote.api

import retrofit2.http.GET
import woowacourse.shopping.data.remote.dto.coupon.CouponDto

interface CouponService {
    @GET("/coupons")
    suspend fun requestCoupons(): List<CouponDto>
}
