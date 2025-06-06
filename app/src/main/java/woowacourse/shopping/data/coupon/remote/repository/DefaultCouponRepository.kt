package woowacourse.shopping.data.coupon.remote.repository

import woowacourse.shopping.data.coupon.remote.service.CouponService
import woowacourse.shopping.domain.coupon.Coupon

class DefaultCouponRepository(
    private val couponService: CouponService,
) : CouponRepository {
    override suspend fun getAllCoupons(): Result<List<Coupon>> {
        return runCatching {
            couponService.getAllCoupons().map {
                it.toDomain()
            }
        }
    }

    companion object {
        @Suppress("ktlint:standard:property-naming")
        private var INSTANCE: CouponRepository? = null

        fun initialize(couponService: CouponService) {
            INSTANCE =
                DefaultCouponRepository(
                    couponService = couponService,
                )
        }

        fun get(): CouponRepository = INSTANCE ?: throw IllegalStateException("초기화 되지 않았습니다.")
    }
}
