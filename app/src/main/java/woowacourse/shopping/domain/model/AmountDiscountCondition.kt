package woowacourse.shopping.domain.model

class AmountDiscountCondition(private val minimumPrice: Int) : DiscountCondition() {
    override fun isSatisfied(
        coupon: Coupon,
        cartItems: List<CartItem>,
    ): Boolean {
        return coupon.totalOrderPrice(cartItems) >= minimumPrice
    }
}
