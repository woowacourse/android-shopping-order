package woowacourse.shopping.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class AvailableTime(
    val start: String? = null,
    val end: String? = null,
)