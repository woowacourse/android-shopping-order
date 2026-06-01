package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Coupon

interface CouponRepository {
    suspend fun requestCoupons(): CouponRequestResult

    sealed interface CouponRequestResult {
        data class Success(
            val coupons: List<Coupon>,
        ) : CouponRequestResult

        data class Failure(
            val cause: Throwable,
        ) : CouponRequestResult
    }
}
