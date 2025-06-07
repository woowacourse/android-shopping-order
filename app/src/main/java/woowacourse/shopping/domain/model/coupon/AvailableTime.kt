package woowacourse.shopping.domain.model.coupon

import java.time.LocalTime

class AvailableTime(
    val start: LocalTime,
    val end: LocalTime,
) {
    fun isInRange(now: LocalTime): Boolean {
        return now.isAfter(start) && now.isBefore(end)
    }
}
