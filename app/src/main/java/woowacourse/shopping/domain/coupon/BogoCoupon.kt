package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.cart.ShoppingCart
import java.time.LocalDate
import java.time.LocalDateTime

data class BogoCoupon(
    val description: String,
    val expirationDate: LocalDate,
    val buyQuantity: Int,
    val getQuantity: Int,
    ): Coupon {
    override fun calculateDiscount(cartItems: List<ShoppingCart>): Int {
        val targetItems = cartItems.filter { it.quantity.value >= (buyQuantity + getQuantity) }
        return targetItems.maxOf { it.product.priceValue } * getQuantity
    }

    override fun isAvailable(cartItems: List<ShoppingCart>, now: LocalDateTime): Boolean {
        if (now.toLocalDate().isAfter(expirationDate)) {
            return false
        }
        return cartItems.any { it.quantity.value >= (buyQuantity + getQuantity) }
    }
}

