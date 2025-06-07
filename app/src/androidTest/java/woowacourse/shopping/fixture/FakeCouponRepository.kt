package woowacourse.shopping.fixture

import woowacourse.shopping.data.util.runCatchingDebugLog
import woowacourse.shopping.domain.model.PaymentSummary
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class FakeCouponRepository(
    private val initialCoupons: List<Coupon> = couponsFixture,
) : CouponRepository {
    override suspend fun fetchCoupons(
        paymentSummary: PaymentSummary,
        isFilterAvailable: Boolean,
    ): Result<List<Coupon>> =
        runCatchingDebugLog {
            if (isFilterAvailable) initialCoupons.availableCoupons(paymentSummary) else initialCoupons
        }

    private fun List<Coupon>.availableCoupons(paymentSummary: PaymentSummary): List<Coupon> =
        filter { it.isAvailable(paymentSummary = paymentSummary) }
}
