package woowacourse.shopping.data.product.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Sort(
    @SerialName("empty")
    val empty: Boolean?,
    @SerialName("sorted")
    val sorted: Boolean?,
    @SerialName("unsorted")
    val unsorted: Boolean?
)