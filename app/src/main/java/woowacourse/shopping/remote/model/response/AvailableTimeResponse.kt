package woowacourse.shopping.remote.model.response

import woowacourse.shopping.data.model.AvailableTimeData

data class AvailableTimeResponse(
    val start: String,
    val end: String,
)

fun AvailableTimeResponse?.toData(): AvailableTimeData? {
    if (this == null) {
        return null
    }
    return AvailableTimeData(
        startTime = this.start,
        endTime = this.end,
    )
}
