package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.toDomain
import woowacourse.shopping.data.source.CouponDataSource
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.Orders

class DefaultCouponRepository(
    private val couponSource: CouponDataSource,
) : CouponRepository {
    override suspend fun availableCoupons(orders: Orders): Result<List<Coupon>> {
        return couponSource.couponsData().mapCatching { couponDataList ->
            val coupons = couponDataList.map {
                it.toDomain()
            }

            val notExpiredCoupons = coupons.filter { coupon ->
                coupon.isExpired(orders).not()
            }

            notExpiredCoupons.filter { coupon ->
                coupon.isSatisfiedPolicy(orders)
            }
        }
    }

    override suspend fun discountAmount(couponId: Long, orders: Orders): Result<Int> {
        val coupon = couponSource.coupon(couponId).mapCatching { couponData ->
            couponData.toDomain()
        }.getOrThrow()

        return runCatching {
            coupon.discountAmount(orders)
        }
    }

    companion object {
        private const val TAG = "DefaultCouponRepository"
    }

}
