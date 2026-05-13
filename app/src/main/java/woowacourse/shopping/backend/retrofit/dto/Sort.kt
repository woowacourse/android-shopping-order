package woowacourse.shopping.backend.retrofit.dto

import kotlinx.serialization.Serializable

@Serializable
data class Sort(
    val empty: Boolean,
    val sorted: Boolean,
    val unsorted: Boolean,
)
