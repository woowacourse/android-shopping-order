package woowacourse.shopping.domain.model.coupon.condition

import woowacourse.shopping.domain.model.AvailableTime
import woowacourse.shopping.domain.model.CartItem
import java.time.LocalTime

class TimeDiscountCondition(private val availableTime: AvailableTime) : DiscountCondition() {
    override fun isSatisfied(
        cartItems: List<CartItem>,
        currentTime: LocalTime,
    ): Boolean {
        return availableTime.available(currentTime)
    }
}
