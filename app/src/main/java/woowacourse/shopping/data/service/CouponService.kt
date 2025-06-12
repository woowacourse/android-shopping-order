package woowacourse.shopping.data.service

import retrofit2.http.GET
import woowacourse.shopping.data.dto.order.Coupon

interface CouponService {
    @GET("/coupons")
    suspend fun requestCoupons(): List<Coupon>
}
