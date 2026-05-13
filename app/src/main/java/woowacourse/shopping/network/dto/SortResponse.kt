package woowacourse.shopping.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class SortResponse(
    val sorted: Boolean,
    val unsorted: Boolean,
    val empty: Boolean
)
