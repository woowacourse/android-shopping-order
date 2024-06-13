package woowacourse.shopping.domain.model.coupon.condition

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Quantity
import java.time.LocalTime

class QuantityDiscountCondition(private val minimumQuantity: Quantity) : DiscountCondition() {
    constructor(minimumQuantity: Int) : this(Quantity(minimumQuantity))

    override fun isSatisfied(
        cartItems: List<CartItem>,
        currentTime: LocalTime,
    ): Boolean {
        return cartItems.any { it.quantity >= minimumQuantity }
    }
}
