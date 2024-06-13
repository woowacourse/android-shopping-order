package woowacourse.shopping.domain.model.coupon.condition

import woowacourse.shopping.domain.model.CartItem
import java.time.LocalTime

class AmountDiscountCondition(private val minimumPrice: Int) : DiscountCondition() {
    override fun isSatisfied(
        cartItems: List<CartItem>,
        currentTime: LocalTime,
    ): Boolean {
        val totalOrderPrice = cartItems.sumOf { it.totalPrice() }
        return totalOrderPrice >= minimumPrice
    }
}
