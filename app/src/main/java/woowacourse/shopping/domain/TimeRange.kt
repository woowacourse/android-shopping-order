package woowacourse.shopping.domain

import java.time.LocalTime

data class TimeRange(
    val from: LocalTime,
    val to: LocalTime,
) {
    fun includes(time: LocalTime): Boolean = time.isAfter(from) && time.isBefore(to)
}
