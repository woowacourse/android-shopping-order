package woowacourse.shopping.data.model.coupon

import kotlinx.serialization.Serializable

@Serializable
data class AvailableTime(
    val start: String,
    val end: String,
)
