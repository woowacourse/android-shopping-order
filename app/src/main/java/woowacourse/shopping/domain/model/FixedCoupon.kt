package woowacourse.shopping.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class FixedCoupon(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val discountType: String,
    override val expirationDate: LocalDate,
    val minimumAmount: Int,
    val discount: Int,
) : Coupon {
    override fun isUsable(
        today: LocalDateTime,
        order: List<Cart>,
        payment: Int,
    ): Boolean {
        if (isExpired(today.toLocalDate())) return false

        if (payment < minimumAmount) return false

        return true
    }

    override fun applyToPayment(
        origin: Payment,
        order: List<Cart>,
    ): Payment {
        val newTotalPayment = (origin.originPayment - discount) + origin.deliveryFee

        return origin.copy(
            couponDiscount = -discount,
            totalPayment = newTotalPayment.coerceAtLeast(0),
        )
    }
}
