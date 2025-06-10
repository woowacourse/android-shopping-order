package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.PaymentSummary
import java.time.LocalDate
import java.time.LocalDateTime

sealed class Coupon {
    abstract val id: Long
    abstract val code: String
    abstract val description: String
    abstract val expirationDate: LocalDate

    abstract fun calculateDiscountAmount(
        paymentSummary: PaymentSummary,
        dateTime: LocalDateTime = LocalDateTime.now(),
    ): PaymentSummary

    open fun isAvailable(
        dateTime: LocalDateTime = LocalDateTime.now(),
        paymentSummary: PaymentSummary,
    ): Boolean = !isExpired(dateTime.toLocalDate())

    private fun isExpired(today: LocalDate = LocalDate.now()): Boolean = expirationDate.isBefore(today)
}
