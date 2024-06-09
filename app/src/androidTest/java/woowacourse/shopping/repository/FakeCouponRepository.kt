package woowacourse.shopping.repository

import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class FakeCouponRepository :
    CouponRepository {
    override suspend fun getAll(): Result<List<Coupon>> {
        TODO("Not yet implemented")
    }
}
