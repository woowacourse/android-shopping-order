package woowacourse.shopping.data.remote.retrofit.dto

import kotlinx.serialization.Serializable

@Serializable
data class AvailableTime(
    val end: String,
    val start: String,
)
