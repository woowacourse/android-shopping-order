package woowacourse.shopping.domain.coupon

import java.time.LocalTime

data class MiracleSaleCoupon(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val discountType: String,
    val availableTime: AvailableTime,
) : Coupon {
    data class AvailableTime(
        val start: LocalTime,
        val end: LocalTime,
    )
}
