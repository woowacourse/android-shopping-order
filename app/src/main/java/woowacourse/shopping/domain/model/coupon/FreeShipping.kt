package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem
import java.time.Instant
import java.util.Date

class FreeShipping(override val coupon: Coupon) : CouponState() {
    override fun createState(coupon: Coupon): CouponState = FreeShipping(coupon)

    override fun isValidCoupon(carts: List<CartItem>): Boolean {
        if (coupon.expirationDate < Date.from(Instant.now())) return false
        val minimumAmount = coupon.minimumAmount ?: return false
        val totalAmount = carts.sumOf { it.totalPrice }
        return minimumAmount <= totalAmount
    }

    override fun calculateDiscount(totalAmount: Int): Int = 3_000
}
