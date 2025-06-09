package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.CartItem
import java.time.LocalDateTime

data class FixedCoupon(
    override val info: CouponInfo,
    override val type: CouponType,
) : Coupon {
    override fun isAvailable(
        cartItems: List<CartItem>,
        now: LocalDateTime,
    ): Boolean {
        if (now.toLocalDate().isAfter(info.expirationDate)) return false
        if (info.minimumAmount != null &&
            cartItems.sumOf { cartItem -> cartItem.totalPrice } < info.minimumAmount
        ) {
            return false
        }
        return true
    }

    override fun discount(cartItems: List<CartItem>): Long = DISCOUNT_AMOUNT

    companion object {
        private const val DISCOUNT_AMOUNT = 5_000L
    }
}
