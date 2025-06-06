package woowacourse.shopping.data.model.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AvailableTimeResponse(
    @SerialName("end")
    val end: String,
    @SerialName("start")
    val start: String,
)
