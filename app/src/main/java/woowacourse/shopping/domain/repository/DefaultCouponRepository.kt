package woowacourse.shopping.domain.repository

import android.util.Log
import woowacourse.shopping.data.model.toDomain
import woowacourse.shopping.data.source.CouponDataSource
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.Orders

class DefaultCouponRepository(
    private val couponSource: CouponDataSource,
) : CouponRepository {
    override suspend fun availableCoupons(orders: Orders): Result<List<Coupon>> {
        return couponSource.couponsData().mapCatching { couponDataList ->

            Log.d(TAG, "orders: $orders")

            val coupons = couponDataList.map {
                it.toDomain()
            }.also {
                Log.d(TAG, "domains: $it")
            }

            val notExpiredCoupons = coupons.filter { coupon ->
                coupon.isExpired(orders).not()
            }.also {
                Log.d(TAG, "filteredCoupons: $it")
            }

            val satisfiedCoupons = notExpiredCoupons.filter { coupon ->
                coupon.isSatisfiedPolicy(orders)
            }.also {
                Log.d(TAG, "satisfiedCoupons: $it")
            }


            satisfiedCoupons
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
