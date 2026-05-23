package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.Order
import java.time.LocalDate
import java.time.LocalDateTime

interface Coupon {
    val code: String
    val description: String
    val expirationDate: LocalDate

    fun isEligible(order: Order): Boolean
    fun calculateDiscount(order: Order): Discount

    fun isExpired(now: LocalDateTime): Boolean = now.toLocalDate().isAfter(expirationDate)
}