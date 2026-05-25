package woowacourse.shopping.backend.retrofit.dto.coupon

import kotlinx.serialization.Serializable

@Serializable
data class AvailableTimeResponse(
    val start: String,
    val end: String,
)
