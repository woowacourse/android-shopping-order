package woowacourse.shopping.domain.model.coupon

import java.time.LocalDate
import java.time.LocalTime

data class Coupon(
    val code: String,
    val description: String = code,
    val type: CouponType,
    val amount: Int? = null,
    val rate: Double? = null,
    val minOrderAmount: Int? = null,
    val expireAt: LocalDate? = null,
    val buyQuantity: Int? = null,
    val getQuantity: Int? = null,
    val availableStartTime: LocalTime? = null,
    val availableEndTime: LocalTime? = null,
) {
    fun isExpired(now: LocalDate): Boolean = expireAt?.let { now.isAfter(it) } ?: false
}
