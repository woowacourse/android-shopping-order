package woowacourse.shopping.data.coupon.remote

import retrofit2.Response
import retrofit2.http.GET
import woowacourse.shopping.data.coupon.remote.dto.CouponDto

interface CouponApiService {
    @GET("/coupons")
    suspend fun requestCoupons(): Response<List<CouponDto>>
}
