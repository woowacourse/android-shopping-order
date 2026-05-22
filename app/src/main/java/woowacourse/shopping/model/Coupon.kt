package woowacourse.shopping.model

import java.time.LocalDate

data class Coupon(
    val id: Long,
    val code: String,
    val title: String,
    val description: String,
    val expirationDate: LocalDate,
    val minimumOrderAmount: Int? = null,
    val fixedDiscountAmount: Int? = null,
    val percentageDiscountRate: Int? = null,
    val requiredSameProductQuantity: Int? = null,
    val freeShipping: Boolean = false,
    val bogoEligible: Boolean = false,
    val availableFromHour: Int? = null,
    val availableToHourExclusive: Int? = null,
)
