package woowacourse.shopping.data.remote.coupon

import retrofit2.http.GET

interface CouponService {
    @GET("/coupons")
    suspend fun requestCoupons(): List<CouponResponse>
}
