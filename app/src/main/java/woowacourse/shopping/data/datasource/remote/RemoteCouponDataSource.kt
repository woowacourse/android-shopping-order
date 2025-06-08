package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.NetworkResultHandler
import woowacourse.shopping.data.network.response.coupons.toDomain
import woowacourse.shopping.data.network.service.CouponService
import woowacourse.shopping.domain.coupon.Coupon

class RemoteCouponDataSource(
    private val couponService: CouponService,
    private val handler: NetworkResultHandler,
) {
    suspend fun getCoupons(): Result<List<Coupon>> =
        handler.handleResult {
            couponService.getCoupons().map { it.toDomain() }
        }
}
