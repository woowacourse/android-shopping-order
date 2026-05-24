package woowacourse.shopping.domain.model.coupon

import java.time.LocalDate

data class Coupon(
    val code: String,
    val type: CouponType,
    val amount: Int? = null,
    val rate: Double? = null,
    val minOrderAmount: Int? = null,
    val expireAt: LocalDate? = null,
) {
    fun isExpired(now: LocalDate): Boolean = expireAt?.let { now.isAfter(it) } ?: false
}


