package woowacourse.shopping.domain.model

class FreeQuantityDiscountPolicy(
    override val discountConditions: List<DiscountCondition>,
    private val minimumQuantity: Quantity,
    private val getQuantity: Quantity,
) : DiscountPolicy {
    override fun discountPrice(
        coupon: Coupon,
        cartItems: List<CartItem>,
    ): Int {
        val maxPriceCartItem =
            cartItems
                .filter { it.quantity >= minimumQuantity }
                .maxBy { it.product.price }
        return maxPriceCartItem.product.price * getQuantity.count
    }
}
