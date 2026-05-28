package woowacourse.shopping.data.network.coupon

import retrofit2.Response
import retrofit2.http.GET
import woowacourse.shopping.data.network.coupon.dto.CouponDto

interface CouponService {
    @GET("/coupons")
    suspend fun getAllCoupons(): Response<List<CouponDto>>
}
