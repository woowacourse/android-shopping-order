package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CouponDataSource
import woowacourse.shopping.data.mapper.toCoupon
import woowacourse.shopping.data.model.response.coupon.CouponResponse
import woowacourse.shopping.domain.coupon.Coupon

class CouponRepositoryImpl(
    private val couponDataSource: CouponDataSource,
) : CouponRepository {
    override suspend fun loadCoupons(): List<Coupon> {
        return couponDataSource.fetchCoupons().map(CouponResponse::toCoupon)
    }
}
