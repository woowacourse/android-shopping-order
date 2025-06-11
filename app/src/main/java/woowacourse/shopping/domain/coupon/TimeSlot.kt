package woowacourse.shopping.domain.coupon

import java.time.LocalDateTime
import java.time.LocalTime

data class TimeSlot(
    val start: LocalTime,
    val end: LocalTime,
) {
    fun isAvailable(now: LocalDateTime): Boolean {
        val time = now.toLocalTime()
        return time.isBefore(end) && time.isAfter(start)
    }
}
