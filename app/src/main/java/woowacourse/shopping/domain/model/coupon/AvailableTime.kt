package woowacourse.shopping.domain.model.coupon

import java.time.LocalTime

data class AvailableTime(
    val start: LocalTime,
    val end: LocalTime,
) {
    fun isAvailableTime(time: LocalTime = LocalTime.now()): Boolean = time.isAfter(start) && time.isBefore(end)

    companion object {
        fun of(
            start: String,
            end: String,
        ): AvailableTime {
            val startTime = LocalTime.parse(start)
            val endTime = LocalTime.parse(end)
            return AvailableTime(startTime, endTime)
        }
    }
}
