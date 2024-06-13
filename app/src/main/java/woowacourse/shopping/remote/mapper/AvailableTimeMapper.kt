package woowacourse.shopping.remote.mapper

import woowacourse.shopping.domain.model.AvailableTime
import woowacourse.shopping.remote.dto.response.AvailableTimeDTO
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun AvailableTimeDTO.toDomain(): AvailableTime {
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    val startTime = LocalTime.parse(start, formatter)
    val endTime = LocalTime.parse(start, formatter)
    return AvailableTime(startTime, endTime)
}
