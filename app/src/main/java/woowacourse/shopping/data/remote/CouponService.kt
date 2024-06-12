package woowacourse.shopping.data.remote

import retrofit2.http.GET
import woowacourse.shopping.data.coupon.remote.CouponDto

interface CouponService {
    @GET("/coupons")
    suspend fun requestCoupons(): Result<List<CouponDto>>
}
