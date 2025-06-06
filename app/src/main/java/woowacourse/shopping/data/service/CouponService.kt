package woowacourse.shopping.data.service

import retrofit2.Response
import retrofit2.http.GET
import woowacourse.shopping.data.model.coupon.CouponResponse

interface CouponService {
    @GET("/coupons")
    suspend fun fetchCoupons(): Response<List<CouponResponse>>
}
