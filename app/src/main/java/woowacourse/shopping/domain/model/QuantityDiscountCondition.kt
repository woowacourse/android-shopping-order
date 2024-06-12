package woowacourse.shopping.domain.model

class QuantityDiscountCondition(private val minimumQuantity: Quantity) : DiscountCondition() {
    override fun isSatisfied(
        coupon: Coupon,
        cartItems: List<CartItem>,
    ): Boolean {
        return cartItems.any { it.quantity >= minimumQuantity }
    }
}
