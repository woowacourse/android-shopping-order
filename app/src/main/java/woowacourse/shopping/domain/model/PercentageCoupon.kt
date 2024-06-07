package woowacourse.shopping.domain.model

import java.time.LocalDate
import java.time.LocalTime

class PercentageCoupon(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    private val discount: Int,
    private val availableTime: AvailableTime,
) : Coupon {
    override fun available(cartItems: List<CartItem>): Boolean {
        return availableTime.available(LocalTime.now())
    }

    override fun discountPrice(cartItems: List<CartItem>): Int {
        return (totalOrderPrice(cartItems) * (discount.toDouble() / 100)).toInt()
    }

    private fun totalOrderPrice(cartItems: List<CartItem>): Int {
        return cartItems.sumOf { it.totalPrice() }
    }
}
