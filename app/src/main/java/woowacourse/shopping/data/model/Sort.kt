package woowacourse.shopping.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Sort(
    val sorted: Boolean,
    val unsorted: Boolean,
    val empty: Boolean,
)
