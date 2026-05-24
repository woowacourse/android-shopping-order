package woowacourse.shopping.model.coupon

import java.time.LocalTime

data class AvailableTime(
    val start: LocalTime,
    val end: LocalTime,
) {
    fun contains(time: LocalTime): Boolean = !time.isBefore(start) && !time.isAfter(end)
}
