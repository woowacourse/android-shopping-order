package woowacourse.shopping.data.source.remote.api

import retrofit2.http.GET
import woowacourse.shopping.data.source.remote.dto.coupon.CouponResponse

interface CouponService {
    @GET("/coupons")
    suspend fun requestCoupons(): List<CouponResponse>
}
