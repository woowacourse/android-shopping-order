package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.PaymentSummary
import java.time.LocalDate
import java.time.LocalDateTime

data class FreeShippingCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    val minimumAmount: Int,
) : Coupon {
    override fun calculateDiscountAmount(
        paymentSummary: PaymentSummary,
        dateTime: LocalDateTime,
    ): PaymentSummary {
        if (!isAvailable(dateTime, paymentSummary)) return paymentSummary
        val totalAmount = paymentSummary.products.sumOf { it.totalPrice }
        if (totalAmount < minimumAmount) return paymentSummary

        return paymentSummary.copy(deliveryFee = 0)
    }
}
