package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.PaymentSummary
import java.time.LocalDate
import java.time.LocalDateTime

data class PercentageDiscountCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    val discount: Int,
    val availableTime: AvailableTime,
) : Coupon() {
    override fun calculateDiscountAmount(
        paymentSummary: PaymentSummary,
        dateTime: LocalDateTime,
    ): PaymentSummary {
        if (!isAvailable(dateTime, paymentSummary)) return paymentSummary

        val totalAmount = paymentSummary.products.sumOf { it.totalPrice }
        val discountAmount = (totalAmount * discount) / 100
        return paymentSummary.copy(discountPrice = discountAmount)
    }

    override fun isAvailable(
        dateTime: LocalDateTime,
        paymentSummary: PaymentSummary,
    ): Boolean {
        val availableTime = availableTime.isAvailable(dateTime.toLocalTime())
        return super.isAvailable(dateTime, paymentSummary) && availableTime
    }
}
