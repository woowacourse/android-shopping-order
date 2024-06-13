package woowacourse.shopping.domain.model.coupon.condition

import woowacourse.shopping.domain.model.CartItem

class AmountDiscountCondition(private val minimumPrice: Int) : DiscountCondition() {
    override fun isSatisfied(cartItems: List<CartItem>): Boolean {
        val totalOrderPrice = cartItems.sumOf { it.totalPrice() }
        return totalOrderPrice >= minimumPrice
    }
}
