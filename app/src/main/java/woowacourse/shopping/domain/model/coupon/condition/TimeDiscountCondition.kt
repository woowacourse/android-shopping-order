package woowacourse.shopping.domain.model.coupon.condition

import woowacourse.shopping.domain.model.AvailableTime
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.coupon.Coupon
import java.time.LocalTime

class TimeDiscountCondition(private val availableTime: AvailableTime) : DiscountCondition() {
    override fun isSatisfied(
        coupon: Coupon,
        cartItems: List<CartItem>,
    ): Boolean {
        return availableTime.available(LocalTime.now())
    }
}
