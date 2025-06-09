package woowacourse.shopping.domain.model.coupon

import java.time.LocalDate

sealed class Coupon {
    abstract val couponBase: CouponBase

    fun isExpired(now: LocalDate = LocalDate.now()): Boolean = now.isAfter(couponBase.expirationDate)
}
