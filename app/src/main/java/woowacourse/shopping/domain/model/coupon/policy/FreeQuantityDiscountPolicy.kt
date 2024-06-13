package woowacourse.shopping.domain.model.coupon.policy

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Quantity
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.condition.DiscountCondition

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
