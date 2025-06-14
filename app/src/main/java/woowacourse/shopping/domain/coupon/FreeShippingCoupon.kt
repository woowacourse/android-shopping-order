package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.CartItem
import woowacourse.shopping.domain.Price
import java.time.LocalDateTime

data class FreeShippingCoupon(
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

    override fun discountAmount(cartItems: List<CartItem>): Long = Price.SHIPPING_FEE
}
