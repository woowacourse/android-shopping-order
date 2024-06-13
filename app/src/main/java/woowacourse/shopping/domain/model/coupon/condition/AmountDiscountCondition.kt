package woowacourse.shopping.domain.model.coupon.condition

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.coupon.Coupon

class AmountDiscountCondition(private val minimumPrice: Int) : DiscountCondition() {
    override fun isSatisfied(
        coupon: Coupon,
        cartItems: List<CartItem>,
    ): Boolean {
        return coupon.totalOrderPrice(cartItems) >= minimumPrice
    }
}
