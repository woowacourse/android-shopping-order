package woowacourse.shopping.data.coupon.source

import woowacourse.shopping.data.coupon.dto.CouponResponse
import woowacourse.shopping.data.coupon.service.CouponService

class RemoteCouponDataSource(
    private val couponService: CouponService
) : CouponDataSource {
    override suspend fun coupons(): CouponResponse? {
        val response: CouponResponse? = couponService.getCoupons()

        return response
    }
}