package woowacourse.shopping.fake

import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class FakeCouponRepository(private val coupons: List<Coupon> = emptyList()) : CouponRepository {
    override suspend fun findAll(): Result<List<Coupon>> {
        return Result.success(coupons)
    }
}
