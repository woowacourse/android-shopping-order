package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CouponDataSource
import woowacourse.shopping.domain.coupon.Coupon

class CouponRepositoryImpl(
    private val couponDataSource: CouponDataSource,
) : CouponRepository {
    override suspend fun loadCoupons(): List<Coupon> {
        return couponDataSource.fetchCoupons()
    }
}
