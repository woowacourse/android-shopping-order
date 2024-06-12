package woowacourse.shopping.remote.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SortResponse(
    val sorted: Boolean,
    @SerialName("unsorted") val unSorted: Boolean,
    val empty: Boolean,
)
