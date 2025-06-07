package woowacourse.shopping.data.dto.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.model.AvailableTime
import java.time.LocalTime

@Serializable
data class AvailableTimeResponse(
    @SerialName("end")
    val end: String,
    @SerialName("start")
    val start: String,
)

fun AvailableTimeResponse.toDomain(): AvailableTime =
    AvailableTime(
        start = LocalTime.parse(this.start),
        end = LocalTime.parse(this.end),
    )
