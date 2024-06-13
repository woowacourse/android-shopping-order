package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem
import java.time.Instant
import java.time.LocalTime
import java.util.Date

class MiracleSale(override val coupon: Coupon) : CouponState() {
    override fun createState(coupon: Coupon): CouponState = MiracleSale(coupon)

    override fun isValidCoupon(carts: List<CartItem>): Boolean {
        if (coupon.expirationDate < Date.from(Instant.now())) return false
        val availableTime = coupon.availableTime ?: return false
        val now = LocalTime.now()
        return now.isAfter(availableTime.start) && now.isBefore(availableTime.end)
    }

    override fun calculateDiscount(totalAmount: Int): Int {
        val discount = coupon.discount ?: return 0
        return (totalAmount * discount * 0.01).toInt()
    }
}
