package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.cart.ShoppingCart
import java.time.LocalDate
import java.time.LocalDateTime

data class FixedCoupon(
    val description: String,
    val expirationDate: LocalDate,
    val discount: Int,
    val minimumAmount: Int,
) : Coupon {
    override fun calculateDiscount(cartItems: List<ShoppingCart>): Int {
        return discount
    }

    override fun isAvailable(
        cartItems: List<ShoppingCart>,
        now: LocalDateTime,
    ): Boolean {
        if (now.toLocalDate().isAfter(expirationDate)) {
            return false
        }
        return minimumAmount <= cartItems.sumOf { it.product.priceValue * it.quantity.value }
    }
}
