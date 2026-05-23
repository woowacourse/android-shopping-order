package woowacourse.shopping.data.source.remote.api

import retrofit2.http.GET
import woowacourse.shopping.data.source.remote.dto.coupon.response.CouponResponse

interface CouponService {
    @GET("/coupons")
    suspend fun getCoupons(): List<CouponResponse>
}
