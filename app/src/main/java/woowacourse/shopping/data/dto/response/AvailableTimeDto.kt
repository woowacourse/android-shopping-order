package woowacourse.shopping.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AvailableTimeDto(
    @SerialName("start")
    val start: String,
    @SerialName("end")
    val end: String,
)
