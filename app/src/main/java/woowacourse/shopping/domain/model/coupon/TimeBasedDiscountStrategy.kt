package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Coupon
import java.time.LocalTime

class TimeBasedDiscountStrategy(
    private val discount: Int,
    private val startTime: LocalTime,
    private val endTime: LocalTime,
) : DiscountStrategy {
    override fun applyDiscount(
        totalPrice: Int,
        coupon: Coupon,
        cartItems: List<CartItem>,
    ): Int {
        val now = LocalTime.now()
        return if (now.isAfter(startTime) && now.isBefore(endTime)) {
            totalPrice - (totalPrice * discount / 100)
        } else {
            totalPrice
        }
    }
}
