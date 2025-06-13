package woowacourse.shopping.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

sealed interface Coupon {
    val id: Int
    val code: String
    val description: String
    val discountType: String
    val expirationDate: LocalDate

    fun isUsable(
        today: LocalDateTime,
        order: Carts,
        payment: Int,
    ): Boolean

    fun applyToPayment(
        origin: Payment,
        order: Carts,
    ): Payment

    fun isExpired(standardDate: LocalDate) = expirationDate.isBefore((standardDate))
}
