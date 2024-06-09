package woowacourse.shopping.repository

import woowacourse.shopping.domain.Coupon
import woowacourse.shopping.domain.CouponRepository

class FakeCouponRepository :
    CouponRepository {
    override suspend fun getCoupons(): Result<List<Coupon>> {
        TODO("Not yet implemented")
    }
}
