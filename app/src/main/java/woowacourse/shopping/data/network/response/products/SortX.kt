package woowacourse.shopping.data.network.response.products

import kotlinx.serialization.SerialName

@Serializable
data class SortX(
    @SerialName("empty")
    val empty: Boolean,
    @SerialName("sorted")
    val sorted: Boolean,
    @SerialName("unsorted")
    val unsorted: Boolean,
)
