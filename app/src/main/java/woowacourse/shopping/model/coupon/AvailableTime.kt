package woowacourse.shopping.model.coupon

import java.time.Clock
import java.time.LocalTime

data class AvailableTime(
    val start: LocalTime,
    val end: LocalTime
) {
    fun isAvailableNow(clock: Clock = Clock.systemDefaultZone()): Boolean {
        val now = LocalTime.now(clock)
        return now in start..end
    }
}
