package woowacourse.shopping.domain.model

sealed interface DiscountPolicy {
    val discountConditions: List<DiscountCondition>

    fun available(
        coupon: Coupon,
        cartItems: List<CartItem>,
    ): Boolean {
        return discountConditions.all { it.available(coupon, cartItems) }
    }

    fun discountPrice(
        coupon: Coupon,
        cartItems: List<CartItem>,
    ): Int
}
