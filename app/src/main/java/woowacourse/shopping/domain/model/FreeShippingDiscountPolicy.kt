package woowacourse.shopping.domain.model

class FreeShippingDiscountPolicy(override val discountConditions: List<DiscountCondition>) : DiscountPolicy {
    override fun discountPrice(
        coupon: Coupon,
        cartItems: List<CartItem>,
    ): Int {
        return Coupon.DELIVERY_FEE
    }
}
