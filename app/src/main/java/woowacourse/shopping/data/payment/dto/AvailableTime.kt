package woowacourse.shopping.data.payment.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AvailableTime(
    @SerialName("end")
    val end: String,
    @SerialName("start")
    val start: String,
)
