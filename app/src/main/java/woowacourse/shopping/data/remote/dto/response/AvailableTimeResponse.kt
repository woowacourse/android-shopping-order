package woowacourse.shopping.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class AvailableTimeResponse(
    val start: String? = null,
    val end: String? = null,
)