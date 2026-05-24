package woowacourse.shopping.fake.repository

import okio.IOException
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class FakeCouponRepository(
    private val coupons: List<Coupon> = emptyList(),
    var shouldFail: Boolean = false,
) : CouponRepository {
    override suspend fun getCoupons(): List<Coupon> {
        if (shouldFail) throw IOException()
        return coupons
    }
}
