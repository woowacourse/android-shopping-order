package woowacourse.shopping.feature.fake

import woowacourse.shopping.data.repository.coupon.CouponRepository
import woowacourse.shopping.domain.coupon.Coupon

class FakeCouponRepository(
    private val coupons: List<Coupon> = emptyList(),
) : CouponRepository {

    override suspend fun getAllCoupons(): List<Coupon> = coupons
}
