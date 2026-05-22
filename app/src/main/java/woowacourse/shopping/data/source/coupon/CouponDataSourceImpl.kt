package woowacourse.shopping.data.source.coupon

import woowacourse.shopping.data.network.coupon.CouponDao
import woowacourse.shopping.domain.coupon.Coupon

class CouponDataSourceImpl(
    private val couponDao: CouponDao
) : CouponDataSource {
    override suspend fun loadCoupons(): List<Coupon> {
        return couponDao.getCoupons()
    }
}
