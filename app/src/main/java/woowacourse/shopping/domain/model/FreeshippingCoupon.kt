package woowacourse.shopping.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class FreeshippingCoupon(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val discountType: String,
    override val expirationDate: LocalDate,
    val minimumAmount: Int,
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
        val newTotalPayment = origin.originPayment + 0

        return origin.copy(
            couponDiscount = 0,
            deliveryFee = 0,
            totalPayment = newTotalPayment,
        )
    }
}
