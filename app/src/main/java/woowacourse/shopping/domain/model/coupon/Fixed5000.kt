package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.Cart
import java.time.Instant
import java.util.Date

class Fixed5000(override val coupon: Coupon) : CouponState() {
    override fun createState(coupon: Coupon): CouponState = Fixed5000(coupon)

    override fun isValidCoupon(carts: List<Cart>): Boolean {
        if (coupon.expirationDate < Date.from(Instant.now())) return false
        val minimumAmount = coupon.minimumAmount ?: return false
        val totalAmount = carts.sumOf { it.totalPrice }
        return minimumAmount <= totalAmount
    }

    override fun calculateDiscount(totalAmount: Int): Int = coupon.discount ?: 0
}
