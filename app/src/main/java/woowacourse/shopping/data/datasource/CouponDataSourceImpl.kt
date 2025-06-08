package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.response.coupon.CouponResponse
import woowacourse.shopping.data.service.CouponService

class CouponDataSourceImpl(
    private val couponService: CouponService,
) : CouponDataSource {
    override suspend fun fetchCoupon(): CouponResponse {
        return couponService.getCoupons()
    }
}
