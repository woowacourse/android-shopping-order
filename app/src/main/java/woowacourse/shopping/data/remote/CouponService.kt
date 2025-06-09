package woowacourse.shopping.data.remote

import retrofit2.Response
import retrofit2.http.GET
import woowacourse.shopping.data.dto.coupon.CouponResponse

interface CouponService {
    @GET("/coupons")
    suspend fun requestCoupons(): Response<List<CouponResponse>>
}
