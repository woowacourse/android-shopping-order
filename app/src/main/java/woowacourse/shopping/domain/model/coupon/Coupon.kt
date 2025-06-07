package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.PaymentSummary
import java.time.LocalDate
import java.time.LocalDateTime

sealed interface Coupon {
    val id: Long
    val code: String
    val description: String
    val expirationDate: LocalDate

    fun calculateDiscountAmount(
        paymentSummary: PaymentSummary,
        dateTime: LocalDateTime = LocalDateTime.now(),
    ): PaymentSummary

    fun isAvailable(
        dateTime: LocalDateTime = LocalDateTime.now(),
        paymentSummary: PaymentSummary,
    ): Boolean = !isExpired(dateTime.toLocalDate())

    private fun isExpired(today: LocalDate = LocalDate.now()): Boolean = expirationDate.isBefore(today)
}
