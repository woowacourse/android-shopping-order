package woowacourse.shopping.data.remote.coupon

import woowacourse.shopping.domain.model.Coupon

class CouponRepository(
    val couponService: CouponService,
) {
    suspend fun getCoupons(): Result<List<Coupon>> =
        runCatching {
            couponService.getCoupons().map { it.toDomain() }
        }
}
