package woowacourse.shopping.domain.model.coupon.policy

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.condition.DiscountCondition
import java.lang.IllegalArgumentException

class PercentDiscountPolicy(
    override val discountConditions: List<DiscountCondition>,
    private val percent: Int,
) : DiscountPolicy {
    override fun discountPrice(
        coupon: Coupon,
        cartItems: List<CartItem>,
    ): Int {
        if (!available(coupon, cartItems)) {
            throw IllegalArgumentException(Coupon.INVALID_DISCOUNT_MESSAGE)
        }
        return (coupon.totalOrderPrice(cartItems) * (percent.toDouble() / 100)).toInt()
    }
}
