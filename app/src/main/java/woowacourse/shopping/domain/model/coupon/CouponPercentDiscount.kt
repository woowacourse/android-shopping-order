package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem
import java.time.LocalTime

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

    override fun getDiscountPrice(cartItems: List<CartItem>): Int {
        val total = cartItems.sumOf { it.totalPrice }
        return (total * (discountPercent / 100.0)).toInt()
    }

    override fun getDiscountDeliveryFee(): Int = 0
}
