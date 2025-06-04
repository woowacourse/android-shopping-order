package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.NetworkResultHandler
import woowacourse.shopping.data.network.service.CouponService
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.exception.NetworkResult

class CouponDataSource(
    private val couponService: CouponService,
    private val handler: NetworkResultHandler,
) {
    suspend fun getCoupons(): NetworkResult<List<Coupon>> =
        handler.execute {
            couponService.getCoupons().map { it.toDomain() }
        }
}
