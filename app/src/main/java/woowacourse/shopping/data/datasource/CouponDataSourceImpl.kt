package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.dto.order.Coupon
import woowacourse.shopping.data.service.CouponService

class CouponDataSourceImpl(
    private val couponService: CouponService,
) : CouponDataSource {
    override suspend fun fetchCoupons(): List<Coupon> = couponService.requestCoupons()
}
