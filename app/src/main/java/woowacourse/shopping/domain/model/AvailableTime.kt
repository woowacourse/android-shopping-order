package woowacourse.shopping.domain.model

import java.time.LocalTime

data class AvailableTime(
    val startTime: LocalTime,
    val endTime: LocalTime
)