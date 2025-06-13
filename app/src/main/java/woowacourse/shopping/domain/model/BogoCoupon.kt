package woowacourse.shopping.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class BogoCoupon(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val discountType: String,
    override val expirationDate: LocalDate,
    val buyQuantity: Int,
    val getQuantity: Int,
) : Coupon {
    override fun isUsable(
        today: LocalDateTime,
        order: Carts,
        payment: Int,
    ): Boolean {
        if (isExpired(today.toLocalDate())) return false
        if (isEnoughQuantity(order).not()) return false
        return true
    }

    override fun applyToPayment(
        origin: Payment,
        order: Carts,
    ): Payment {
        val mostExpensiveProductPrice = order.findTargetProductForBogo(buyQuantity)

        val totalPayment = origin.totalPayment - mostExpensiveProductPrice
        return origin.copy(
            couponDiscount = -mostExpensiveProductPrice,
            totalPayment = totalPayment,
        )
    }

    private fun isEnoughQuantity(order: Carts): Boolean {
        val quantities = order.carts.map { it.cart }.map { it.quantity }
        return quantities.any { it >= buyQuantity + getQuantity }
    }
}
