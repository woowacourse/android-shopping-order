package woowacourse.shopping.data.coupon.remote.service

import retrofit2.http.GET
import woowacourse.shopping.data.coupon.remote.dto.CouponResponse

interface CouponService {
    @GET("coupons")
    suspend fun getAllCoupons(): List<CouponResponse>
}
