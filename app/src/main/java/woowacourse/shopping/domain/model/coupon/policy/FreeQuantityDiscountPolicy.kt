package woowacourse.shopping.domain.model.coupon.policy

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Quantity
import woowacourse.shopping.domain.model.coupon.condition.DiscountCondition

class FreeQuantityDiscountPolicy(
    override val discountConditions: List<DiscountCondition>,
    private val minimumQuantity: Quantity,
    private val freeQuantity: Quantity,
) : DiscountPolicy {
    constructor(
        discountConditions: List<DiscountCondition>,
        minimumQuantity: Int,
        freeQuantity: Int,
    ) : this(discountConditions, Quantity(minimumQuantity), Quantity(freeQuantity))

    override fun discountPrice(cartItems: List<CartItem>): Int {
        val maxPriceCartItem =
            cartItems
                .filter { it.quantity >= minimumQuantity }
                .maxBy { it.product.price }
        return maxPriceCartItem.product.price * freeQuantity.count
    }
}
