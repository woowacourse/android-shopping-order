package woowacourse.shopping.data.remote.dto

import kotlinx.serialization.Serializable
import woowacourse.shopping.model.coupon.AvailableTime
import java.time.LocalTime

@Serializable
data class AvailableTimeResponse(
    val start: String,
    val end: String,
)

fun AvailableTimeResponse.toDomain(): AvailableTime =
    AvailableTime(LocalTime.parse(start), LocalTime.parse(end))
