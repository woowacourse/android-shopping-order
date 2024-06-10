package woowacourse.shopping.data.model

import woowacourse.shopping.domain.model.AvailableTime
import java.time.LocalTime

data class AvailableTimeData(
    val startTime: String,
    val endTime: String
)

fun AvailableTimeData.toDomain() = AvailableTime(
    startTime = LocalTime.parse(startTime),
    endTime = LocalTime.parse(endTime)
)