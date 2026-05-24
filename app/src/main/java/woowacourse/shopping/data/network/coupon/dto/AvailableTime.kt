package woowacourse.shopping.data.network.coupon.dto

import kotlinx.serialization.Serializable

@Serializable
data class AvailableTime(
    val start: String?,
    val end: String?,
)
