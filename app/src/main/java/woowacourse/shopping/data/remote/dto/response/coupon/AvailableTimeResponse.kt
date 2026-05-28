package woowacourse.shopping.data.remote.dto.response.coupon

import kotlinx.serialization.Serializable

@Serializable
data class AvailableTimeResponse(
    val start: String,
    val end: String,
)
