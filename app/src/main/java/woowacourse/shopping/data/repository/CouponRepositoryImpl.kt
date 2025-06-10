package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.remote.CouponRemoteDataSource
import woowacourse.shopping.data.model.coupon.toDomain
import woowacourse.shopping.data.util.result.mapCatchingDebugLog
import woowacourse.shopping.domain.model.PaymentSummary
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class CouponRepositoryImpl(
    private val couponRemoteDataSource: CouponRemoteDataSource,
) : CouponRepository {
    override suspend fun fetchCoupons(
        paymentSummary: PaymentSummary,
        isFilterAvailable: Boolean,
    ): Result<List<Coupon>> =
        couponRemoteDataSource
            .fetchCoupons()
            .mapCatchingDebugLog { rawCoupons ->
                rawCoupons.map { rawCoupon -> rawCoupon.toDomain() }
            }.mapCatchingDebugLog { coupons ->
                if (isFilterAvailable) coupons.availableCoupons(paymentSummary) else coupons
            }

    private fun List<Coupon>.availableCoupons(paymentSummary: PaymentSummary): List<Coupon> =
        filter { it.isAvailable(paymentSummary = paymentSummary) }
}
