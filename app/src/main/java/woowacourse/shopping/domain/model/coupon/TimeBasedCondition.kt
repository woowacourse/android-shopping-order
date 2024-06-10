package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.AvailableTime
import woowacourse.shopping.domain.model.Cart
import java.time.Instant
import java.time.LocalTime
import java.util.Date

class TimeBasedCondition(
    private val expirationDate: Date?,
    private val availableTime: AvailableTime?,
) : CouponCondition {
    override fun isValid(carts: List<Cart>): Boolean {
        expirationDate ?: return false
        availableTime ?: return false

        if (expirationDate.before(Date.from(Instant.now()))) return false
        val now = LocalTime.now()
        return now.isAfter(availableTime.start) && now.isBefore(availableTime.end)
    }
}
