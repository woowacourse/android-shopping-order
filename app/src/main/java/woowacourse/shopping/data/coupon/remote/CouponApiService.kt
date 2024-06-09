package woowacourse.shopping.data.coupon.remote

import retrofit2.Response
import retrofit2.http.GET
import woowacourse.shopping.data.coupon.remote.dto.CouponResponse

interface CouponApiService {
    @GET("/coupons")
    suspend fun requestCoupons(): Response<CouponResponse>
}
