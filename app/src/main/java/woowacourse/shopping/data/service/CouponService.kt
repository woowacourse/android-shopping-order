package woowacourse.shopping.data.service

import retrofit2.Response
import retrofit2.http.GET
import woowacourse.shopping.data.dto.coupon.CouponResponseItem

interface CouponService {
    @GET("/coupons")
    suspend fun requestCoupons(): Response<List<CouponResponseItem>>
}
