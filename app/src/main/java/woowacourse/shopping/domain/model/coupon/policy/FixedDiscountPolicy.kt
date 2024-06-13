package woowacourse.shopping.domain.model.coupon.policy

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.condition.DiscountCondition

class FixedDiscountPolicy(
    override val discountConditions: List<DiscountCondition>,
    private val discount: Int,
) : DiscountPolicy {
    override fun discountPrice(
        coupon: Coupon,
        cartItems: List<CartItem>,
    ): Int {
        return discount
    }
}
