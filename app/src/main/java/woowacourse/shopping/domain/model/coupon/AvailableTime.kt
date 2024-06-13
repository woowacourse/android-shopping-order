package woowacourse.shopping.domain.model.coupon

import java.time.LocalTime

data class AvailableTime(
    val start: LocalTime,
    val end: LocalTime,
) {
    fun isAvailableTime(time: LocalTime): Boolean = time.isAfter(start) && time.isBefore(end)
}
