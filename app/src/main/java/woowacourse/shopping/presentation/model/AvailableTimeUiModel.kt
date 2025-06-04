package woowacourse.shopping.presentation.model

import woowacourse.shopping.domain.model.AvailableTime
import java.time.format.DateTimeFormatter

data class AvailableTimeUiModel(
    val timeRange: String,
)

fun AvailableTime.toPresentation(): AvailableTimeUiModel {
    val timeRange = "${start.format(DateTimeFormatter.ofPattern("HH:mm"))} ~ ${
        end.format(
            DateTimeFormatter.ofPattern("HH:mm"),
        )
    }"
    return AvailableTimeUiModel(timeRange)
}
