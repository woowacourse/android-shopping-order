package woowacourse.shopping.data.network.response.carts


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SortX(
    val empty: Boolean,
    val sorted: Boolean,
    @SerialName("unsorted")
    val unSorted: Boolean
)
