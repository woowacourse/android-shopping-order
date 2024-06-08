package woowacourse.shopping.data.coupon.remote

import woowacourse.shopping.data.remote.ApiClient
import woowacourse.shopping.domain.Coupon

class RemoteCouponDataSource {
    private val couponApiService: CouponApiService =
        ApiClient.getApiClient().create(CouponApiService::class.java)

    suspend fun requestCoupon(): List<Coupon> {
        return couponApiService.requestCoupons().map { it.toDomain() }
    }
}
