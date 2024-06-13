package woowacourse.shopping.domain.model.coupon.policy

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.condition.DiscountCondition

class FreeShippingDiscountPolicy(override val discountConditions: List<DiscountCondition>) : DiscountPolicy {
    override fun discountPrice(cartItems: List<CartItem>): Int {
        return Coupon.DELIVERY_FEE
    }
}
