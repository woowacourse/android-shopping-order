package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.local.CouponLocalDataSource
import woowacourse.shopping.data.datasource.remote.CouponRemoteDataSource
import woowacourse.shopping.data.model.coupon.toDomain
import woowacourse.shopping.data.util.runCatchingDebugLog
import woowacourse.shopping.domain.model.PaymentSummary
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class CouponRepositoryImpl(
    private val couponRemoteDataSource: CouponRemoteDataSource,
    private val couponLocalDataSource: CouponLocalDataSource,
) : CouponRepository {
    override suspend fun fetchCoupons(
        paymentSummary: PaymentSummary,
        isFilterAvailable: Boolean,
    ): Result<List<Coupon>> =
        runCatchingDebugLog {
            val response = couponRemoteDataSource.fetchCoupons().getOrDefault(emptyList())
            val coupons = response.map { it.toDomain() }
            couponLocalDataSource.saveCoupons(coupons)

            if (isFilterAvailable) coupons.availableCoupons(paymentSummary) else coupons
        }

    private fun List<Coupon>.availableCoupons(paymentSummary: PaymentSummary): List<Coupon> =
        filter { it.isAvailable(paymentSummary = paymentSummary) }
}
