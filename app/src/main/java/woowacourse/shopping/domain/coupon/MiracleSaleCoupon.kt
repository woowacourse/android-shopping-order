package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.cart.ShoppingCart
import java.time.LocalDate
import java.time.LocalDateTime

data class MiracleSaleCoupon(
    val description: String,
    val expirationDate: LocalDate,
    val discount: Int,
    val availableTime: AvailableTime,
) : Coupon {
    override fun calculateDiscount(cartItems: List<ShoppingCart>): Int {
        val totalPrice = cartItems.sumOf { (it.product.priceValue * it.quantity.value) }
        return (totalPrice * discount / 100)
    }

    override fun isAvailable(
        cartItems: List<ShoppingCart>,
        now: LocalDateTime,
    ): Boolean {
        if (now.toLocalDate().isAfter(expirationDate)) {
            return false
        }
        return availableTime.isAvailable(now)
    }
}
