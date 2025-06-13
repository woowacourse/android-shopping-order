package woowacourse.shopping.data.datasource.coupon

import woowacourse.shopping.data.mapper.toCoupon
import woowacourse.shopping.data.model.response.coupon.CouponResponse
import woowacourse.shopping.data.service.CouponService
import woowacourse.shopping.domain.coupon.Coupon

class CouponDataSourceImpl(
    private val couponService: CouponService,
) : CouponDataSource {
    override suspend fun fetchCoupons(): List<Coupon> {
        return couponService.getCoupons().map(CouponResponse::toCoupon)
    }
}
