package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.CartItem
import java.time.LocalDateTime

data class BuyXGetYFreeCoupon(override val info: CouponInfo, override val type: CouponType) :
    Coupon {
    override fun isAvailable(
        cartItems: List<CartItem>,
        now: LocalDateTime,
    ): Boolean {
        if (now.toLocalDate().isAfter(info.expirationDate)) return false
        if (info.buyQuantity != null && info.getQuantity != null &&
            cartItems.none { cartItem -> cartItem.quantity >= info.buyQuantity + info.getQuantity }
        ) {
            return false
        }
        return true
    }

    override fun discount(cartItems: List<CartItem>): Long {
        val requiredQuantity = ((info.buyQuantity ?: 0) + (info.getQuantity ?: 0)).coerceAtLeast(1)
        val bonusQuantity = info.getQuantity ?: 0
        val applicableItem =
            cartItems
                .filter { cartItem -> cartItem.quantity >= requiredQuantity }
                .maxBy { cartItem -> cartItem.product.price.value }
        return (applicableItem.quantity / requiredQuantity * bonusQuantity) * applicableItem.product.price.value
    }
}
