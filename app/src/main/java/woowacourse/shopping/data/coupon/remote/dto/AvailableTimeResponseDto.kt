package woowacourse.shopping.data.coupon.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AvailableTimeResponseDto(
    @SerialName("end")
    val end: String,
    @SerialName("start")
    val start: String,
)
