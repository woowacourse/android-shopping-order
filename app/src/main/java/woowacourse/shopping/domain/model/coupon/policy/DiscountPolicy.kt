package woowacourse.shopping.domain.model.coupon.policy

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.condition.DiscountCondition

sealed interface DiscountPolicy {
    val discountConditions: List<DiscountCondition>

    fun available(
        coupon: Coupon,
        cartItems: List<CartItem>,
    ): Boolean {
        return discountConditions.all { it.available(expirationDate = coupon.expirationDate, cartItems = cartItems) }
    }

    fun discountPrice(cartItems: List<CartItem>): Int
}
