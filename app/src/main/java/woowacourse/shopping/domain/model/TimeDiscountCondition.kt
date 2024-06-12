package woowacourse.shopping.domain.model

import java.time.LocalTime

class TimeDiscountCondition(private val availableTime: AvailableTime) : DiscountCondition() {
    override fun isSatisfied(
        coupon: Coupon,
        cartItems: List<CartItem>,
    ): Boolean {
        return availableTime.available(LocalTime.now())
    }
}
