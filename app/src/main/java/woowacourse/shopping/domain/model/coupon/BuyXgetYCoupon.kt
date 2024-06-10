package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem
import java.time.LocalDate

class BuyXgetYCoupon(override val coupon: Coupon) : CouponState(coupon) {
    override fun isValid(cartItems: List<CartItem>): Boolean {
        val now = LocalDate.now()
        if (now > coupon.expirationDate) return false

        val cartItemsWithThreeOrMoreQuantity: List<CartItem> =
            cartItems.filter { cartItem -> cartItem.quantity >= 3 }
        return cartItemsWithThreeOrMoreQuantity.isNotEmpty()
    }

    override fun discountPrice(cartItems: List<CartItem>): Int {
        val cartItemsWithThreeOrMoreQuantity: List<CartItem> =
            cartItems.filter { cartItem -> cartItem.quantity >= 3 }
        return cartItemsWithThreeOrMoreQuantity.maxOf { cartItem -> cartItem.product.price }
    }
}
