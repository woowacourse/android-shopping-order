package woowacourse.shopping.domain.model

import java.time.LocalTime

class AvailableTime(
    private val start: LocalTime,
    private val end: LocalTime,
) {
    fun available(time: LocalTime): Boolean {
        if (start == time) return true
        if (end == time) return true
        return start.isAfter(time) && end.isBefore(time)
    }
}
