package woowacourse.shopping.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AvailableTime(
    @SerialName("start")
    val start: String,
    @SerialName("end")
    val end: String,
)
