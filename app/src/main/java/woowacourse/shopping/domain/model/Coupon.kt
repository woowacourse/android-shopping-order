package woowacourse.shopping.domain.model

import woowacourse.shopping.domain.model.coupon.DiscountStrategy
import java.time.LocalDate
import java.time.LocalTime

data class Coupon(
    val id: Long,
    val code: String,
    val description: String,
    val expirationDate: LocalDate,
    val discountType: String,
    val minimumAmount: Int = 0,
    val discount: Int? = null,
    val buyQuantity: Int? = null,
    val getQuantity: Int? = null,
    val availableTime: AvailableTime? = null,
    val discountStrategy: DiscountStrategy,
) {
    data class AvailableTime(
        val start: LocalTime,
        val end: LocalTime,
    )
}
