package woowacourse.shopping.domain.model

import java.time.LocalDate
import java.time.LocalTime

data class Coupon(
    val id: Long,
    val code: String,
    val description: String,
    val expirationDate: LocalDate,
    val discount: Int?,
    val minimumAmount: Int?,
    val buyQuantity: Int?,
    val getQuantity: Int?,
    val availableTime: AvailableLocalTime?,
    val discountType: String,
)

data class AvailableLocalTime(
    val start: LocalTime,
    val end: LocalTime,
)
