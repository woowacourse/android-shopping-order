package woowacourse.shopping.data.coupon.source

import woowacourse.shopping.data.coupon.dto.CouponResponseItem
import woowacourse.shopping.data.coupon.service.CouponService

class RemoteCouponDataSource(
    private val couponService: CouponService
) : CouponDataSource {
    override suspend fun coupons(): List<CouponResponseItem>? {
        val response: List<CouponResponseItem>? = couponService.getCoupons()

        return response
    }
}