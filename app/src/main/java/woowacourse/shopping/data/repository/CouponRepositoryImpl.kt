package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CouponDataSource
import woowacourse.shopping.data.mapper.toCoupon
import woowacourse.shopping.domain.Coupon

class CouponRepositoryImpl(
    private val couponDataSource: CouponDataSource,
) : CouponRepository {
    override suspend fun loadCoupons(): Coupon {
        return couponDataSource.fetchCoupon().toCoupon()
    }
}
