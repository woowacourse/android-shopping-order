package woowacourse.shopping.data.model.coupon

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

fun AvailableTimeResponse.toDomain(): AvailableTime = AvailableTime(start = LocalTime.parse(start), end = LocalTime.parse(end))
