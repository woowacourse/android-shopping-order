package woowacourse.shopping.data.dto.response

import woowacourse.shopping.domain.AvailableTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class AvailableTimeResponse(
    val start: String,
    val end: String,
) {
    fun toDomain(): AvailableTime =
        AvailableTime(
            start = toLocalTime(start),
            end = toLocalTime(end),
        )

    private fun toLocalTime(timeString: String): LocalTime {
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        return LocalTime.parse(timeString, formatter)
    }
}
