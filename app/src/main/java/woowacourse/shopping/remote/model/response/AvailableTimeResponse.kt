package woowacourse.shopping.remote.model.response

import kotlinx.serialization.Serializable

@Serializable
data class AvailableTimeResponse(
    val start: String,
    val end: String,
)
