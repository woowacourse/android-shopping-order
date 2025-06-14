package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.Payment
import woowacourse.shopping.domain.cart.ShoppingCarts
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
        order: ShoppingCarts,
        payment: Int,
    ): Boolean {
        if (isExpired(today.toLocalDate())) return false
        if (hasEnoughQuantity(order).not()) return false
        return true
    }

    override fun applyToPayment(
        origin: Payment,
        order: ShoppingCarts,
    ): Payment {
        val mostExpensiveProductPrice = order.mostExpensiveCartPriceWithStandardQuantity(buyQuantity)

        val totalPayment = origin.totalPayment - mostExpensiveProductPrice
        return origin.copy(
            couponDiscount = -mostExpensiveProductPrice,
            totalPayment = totalPayment,
        )
    }

    private fun hasEnoughQuantity(order: ShoppingCarts): Boolean {
        val quantities = order.shoppingCarts.map { it.quantity }
        return quantities.any { it.value >= buyQuantity + getQuantity }
    }
}
