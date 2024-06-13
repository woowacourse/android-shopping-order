package woowacourse.shopping.domain.model.coupon.policy

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.coupon.condition.DiscountCondition

class PercentDiscountPolicy(
    override val discountConditions: List<DiscountCondition>,
    private val percent: Int,
) : DiscountPolicy {
    override fun discountPrice(cartItems: List<CartItem>): Int {
        val totalOrderPrice = cartItems.sumOf { it.totalPrice() }
        return (totalOrderPrice * (percent.toDouble() / 100)).toInt()
    }
}
