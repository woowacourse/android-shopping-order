package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.PaymentSummary
import woowacourse.shopping.domain.model.coupon.Coupon

interface CouponRepository {
    suspend fun fetchCoupons(
        paymentSummary: PaymentSummary,
        isFilterAvailable: Boolean,
    ): Result<List<Coupon>>
}
