package woowacourse.shopping.domain.model

import java.time.LocalTime

data class AvailableTime(val start: LocalTime, val end: LocalTime) {
    init {
        require(start <= end)
    }

    fun isWithinTimeRange(inputTime: LocalTime): Boolean {
        return inputTime in start..end
    }
}
