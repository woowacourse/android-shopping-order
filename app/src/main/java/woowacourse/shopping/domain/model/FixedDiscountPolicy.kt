package woowacourse.shopping.domain.model

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
