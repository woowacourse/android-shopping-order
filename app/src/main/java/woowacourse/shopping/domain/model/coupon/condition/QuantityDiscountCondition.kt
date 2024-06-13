package woowacourse.shopping.domain.model.coupon.condition

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Quantity

class QuantityDiscountCondition(private val minimumQuantity: Quantity) : DiscountCondition() {
    override fun isSatisfied(cartItems: List<CartItem>): Boolean {
        return cartItems.any { it.quantity >= minimumQuantity }
    }
}
