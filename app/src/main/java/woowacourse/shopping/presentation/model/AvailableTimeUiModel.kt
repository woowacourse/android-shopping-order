package woowacourse.shopping.presentation.model

import woowacourse.shopping.domain.model.AvailableTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class AvailableTimeUiModel(
    val timeRange: String,
)

fun AvailableTime.toPresentation(pattern: String = "HH시 mm분"): AvailableTimeUiModel {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    val timeRange = "${start.format(formatter)} ~ ${end.format(formatter)}"
    return AvailableTimeUiModel(timeRange)
}

fun AvailableTimeUiModel.toDomain(pattern: String = "HH시 mm분"): AvailableTime {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    val (startStr, endStr) = timeRange.split(" ~ ")
    return AvailableTime(
        start = LocalTime.parse(startStr, formatter),
        end = LocalTime.parse(endStr, formatter),
    )
}
