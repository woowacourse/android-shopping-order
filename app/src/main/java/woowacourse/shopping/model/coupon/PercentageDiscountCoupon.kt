package woowacourse.shopping.model.coupon

import java.time.LocalDate
import java.time.LocalTime

data class PercentageDiscountCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    val discountRate: Int,
    val availableTime: AvailableTime,
) : Coupon

data class AvailableTime(
    val start: LocalTime,
    val end: LocalTime,
)
