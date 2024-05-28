package woowacourse.shopping.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class Sort(
    val sorted: Boolean,
    val unsorted: Boolean,
    val empty: Boolean
)
