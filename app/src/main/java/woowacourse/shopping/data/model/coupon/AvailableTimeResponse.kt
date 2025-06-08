package woowacourse.shopping.data.model.coupon

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import woowacourse.shopping.domain.model.AvailableTime
import java.time.LocalTime

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("discountType")
data class AvailableTimeResponse(
    @SerialName("end")
    val end: String,
    @SerialName("start")
    val start: String,
)

fun AvailableTimeResponse.toDomain(): AvailableTime = AvailableTime(start = LocalTime.parse(start), end = LocalTime.parse(end))
