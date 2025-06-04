package woowacourse.shopping.domain.model

import java.time.LocalDate
import java.time.LocalTime

data class Coupon(
    val id: Int,
    val code: CouponType,
    val name: String,
    val expirationDate: LocalDate,
    val discount: Int?,
    val minimumAmount: Int?,
    val discountType: String,
    val buyQuantity: Int?,
    val getQuantity: Int?,
    val availableTime: AvailableTime?,
) {
    data class AvailableTime(
        val start: LocalTime,
        val end: LocalTime,
    )
}
