package woowacourse.shopping.repository.http.dto.coupon

import kotlinx.serialization.Serializable

@Serializable
data class AvailableTimeDto(
    val start: String,
    val end: String,
)
