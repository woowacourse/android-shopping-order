package woowacourse.shopping.domain.model

import java.time.LocalTime

class AvailableTime(
    private val start: LocalTime,
    private val end: LocalTime,
) {
    init {
        require(start.isBefore(end)) { INVALID_AVAILABLE_TIME_RANGE_MESSAGE }
    }

    fun available(time: LocalTime): Boolean {
        if (start == time) return true
        if (end == time) return true
        return start.isBefore(time) && end.isAfter(time)
    }

    companion object {
        private const val INVALID_AVAILABLE_TIME_RANGE_MESSAGE = "시작 시간은 끝 시간보다 이전이어야 합니다."
    }
}
