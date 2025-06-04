package woowacourse.shopping.domain.model

import java.time.LocalDate
import java.time.LocalTime

data class Coupon(
    val id: Int,
    val code: CouponCodeType,
    val name: String,
    val expirationDate: LocalDate,
    val discount: Int?,
    val minimumPurchase: Int?,
    val discountType: CouponDiscountType,
    val buyQuantity: Int?,
    val getQuantity: Int?,
    val availableTime: AvailableTime?,
    val isSelected: Boolean = false,
) {
    data class AvailableTime(
        val start: LocalTime,
        val end: LocalTime,
    )
}
