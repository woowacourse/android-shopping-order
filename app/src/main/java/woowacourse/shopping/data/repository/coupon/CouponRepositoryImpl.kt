package woowacourse.shopping.data.repository.coupon

import woowacourse.shopping.data.source.coupon.CouponDataSource
import woowacourse.shopping.domain.coupon.Coupon

class CouponRepositoryImpl(
    private val couponDataSource: CouponDataSource,
) : CouponRepository {
    override suspend fun loadCoupons(): List<Coupon> = couponDataSource.loadCoupons()
}
