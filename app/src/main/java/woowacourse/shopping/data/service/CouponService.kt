package woowacourse.shopping.data.service

import retrofit2.Response
import retrofit2.http.GET
import woowacourse.shopping.data.dto.coupon.Coupon

interface CouponService {
    @GET("/coupons")
    suspend fun getCoupons(): Response<List<Coupon>>
}
