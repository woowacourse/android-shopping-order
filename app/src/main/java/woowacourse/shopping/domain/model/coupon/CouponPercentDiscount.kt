package woowacourse.shopping.domain.model.coupon

import java.time.LocalTime
import woowacourse.shopping.domain.model.CartItem

data class CouponPercentDiscount(
    override val couponBase: CouponBase,
    val discountPercent: Int,
    val availableStartTime: LocalTime,
    val availableEndTime: LocalTime,
    val discountType: CouponDiscountType,
) : Coupon() {
    override fun isAvailable(cartItems: List<CartItem>): Boolean {
        val nowTime = LocalTime.now()
        return !isExpired() &&
            nowTime.isAfter(availableStartTime) &&
            nowTime.isBefore(availableEndTime)
    }
}
