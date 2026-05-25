package woowacourse.shopping.data.source.coupon

import woowacourse.shopping.data.network.coupon.RetrofitCouponService
import woowacourse.shopping.data.network.coupon.dto.toDomain
import woowacourse.shopping.domain.coupon.Coupon

class CouponDataSourceImpl(
    private val couponService: RetrofitCouponService,
) : CouponDataSource {
    override suspend fun loadCoupons(): List<Coupon> {
        val body = couponService.requestCoupons()
        return body.map { it.toDomain() }
    }
}
