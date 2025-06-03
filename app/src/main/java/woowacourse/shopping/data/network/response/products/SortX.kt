package woowacourse.shopping.data.network.response.products

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SortX(
    @SerialName("empty")
    val empty: Boolean,
    @SerialName("sorted")
    val sorted: Boolean,
    @SerialName("unsorted")
    val unsorted: Boolean,
)
