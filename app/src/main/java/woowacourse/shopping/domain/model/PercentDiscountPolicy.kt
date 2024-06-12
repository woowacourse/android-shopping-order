package woowacourse.shopping.domain.model

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
