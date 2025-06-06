package woowacourse.shopping.data.model.coupon

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("discountType")
data class AvailableTimeResponse(
    @SerialName("end")
    val end: String,
    @SerialName("start")
    val start: String,
)

fun AvailableTimeResponse.toLocalDate(): LocalDate {
    val dateString = "2025-06-30"
    return LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE)
}
