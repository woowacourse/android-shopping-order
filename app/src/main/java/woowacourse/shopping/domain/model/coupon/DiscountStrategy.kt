package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Coupon

interface DiscountStrategy {
    fun applyDiscount(
        totalPrice: Int,
        coupon: Coupon,
        cartItems: List<CartItem>,
    ): Int
}
