package woowacourse.shopping.domain.coupon

import java.time.LocalDate
import java.time.LocalTime

data class MiracleSaleCoupon(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val discountType: String,
    override val expirationDate: LocalDate,
    val availableTime: AvailableTime,
) : Coupon {
    data class AvailableTime(
        val start: LocalTime,
        val end: LocalTime,
    )
}
