package woowacourse.shopping.data.network.response.coupon

import kotlinx.serialization.Serializable

@Serializable
data class AvailableTime(
    val end: String,
    val start: String
)
