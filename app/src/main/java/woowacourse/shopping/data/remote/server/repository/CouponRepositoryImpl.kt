package woowacourse.shopping.data.remote.server.repository

import woowacourse.shopping.data.remote.server.dto.coupon.toDomainCoupon
import woowacourse.shopping.data.remote.server.service.CouponService
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class CouponRepositoryImpl(
    private val couponService: CouponService,
) : CouponRepository {
    override suspend fun getCoupons(): List<Coupon> = couponService.requestCoupons().map { it.toDomainCoupon() }
}
