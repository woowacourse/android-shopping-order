package woowacourse.shopping.data.source.remote.datasource

import retrofit2.Retrofit
import woowacourse.shopping.data.source.remote.api.CouponService
import woowacourse.shopping.data.source.remote.dto.coupon.CouponResponse

class CouponRemoteDataSource(
    retrofit: Retrofit,
) {
    private val couponService = retrofit.create(CouponService::class.java)

    suspend fun getCoupons(): List<CouponResponse> = couponService.requestCoupons()
}
