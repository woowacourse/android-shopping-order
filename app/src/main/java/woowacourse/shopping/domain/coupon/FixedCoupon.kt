package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.Payment
import woowacourse.shopping.domain.cart.ShoppingCarts
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
        order: ShoppingCarts,
        payment: Int,
    ): Boolean {
        if (isExpired(today.toLocalDate())) return false
        if (!isMinimumAmountSatisfied(payment)) return false
        return true
    }

    override fun applyToPayment(
        origin: Payment,
        order: ShoppingCarts,
    ): Payment {
        val totalPayment = origin.totalPayment - discount
        return origin.copy(
            couponDiscount = -discount,
            totalPayment = totalPayment,
        )
    }

    private fun isMinimumAmountSatisfied(standardAmount: Int) = standardAmount >= minimumAmount
}
