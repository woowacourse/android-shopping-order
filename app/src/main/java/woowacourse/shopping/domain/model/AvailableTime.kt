package woowacourse.shopping.domain.model

import java.time.LocalTime

data class AvailableTime(
    val start: LocalTime,
    val end: LocalTime,
) {
    fun isAvailable(time: LocalTime) = time in start..end
}
