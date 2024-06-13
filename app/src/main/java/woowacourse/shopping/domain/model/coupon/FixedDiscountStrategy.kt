package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Coupon

class FixedDiscountStrategy(private val discount: Int) : DiscountStrategy {
    override fun applyDiscount(
        totalPrice: Int,
        coupon: Coupon,
        cartItems: List<CartItem>,
    ): Int {
        return totalPrice - discount
    }
}
