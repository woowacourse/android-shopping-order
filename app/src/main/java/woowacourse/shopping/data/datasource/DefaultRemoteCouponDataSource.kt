package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.coupon.CouponResponseItem
import woowacourse.shopping.data.remote.CouponService

class DefaultRemoteCouponDataSource(
    private val couponService: CouponService,
) : RemoteCouponDataSource {
    override suspend fun getCoupons(): List<CouponResponseItem> {
        return couponService.getCoupons()
    }
}
