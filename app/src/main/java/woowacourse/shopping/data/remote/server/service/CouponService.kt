package woowacourse.shopping.data.remote.server.service

import retrofit2.http.GET
import woowacourse.shopping.data.remote.server.dto.coupon.CouponResponse

interface CouponService {
    @GET("coupons")
    suspend fun requestCoupons(): List<CouponResponse>
}
