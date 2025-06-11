package woowacourse.shopping.data.dto.response.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.model.coupon.AvailableTime
import java.time.LocalTime

@Serializable
data class AvailableTimeDto(
    @SerialName("start")
    val start: String,
    @SerialName("end")
    val end: String,
)

fun AvailableTimeDto.toDomain(): AvailableTime =
    AvailableTime(
        LocalTime.parse(start),
        LocalTime.parse(end),
    )
