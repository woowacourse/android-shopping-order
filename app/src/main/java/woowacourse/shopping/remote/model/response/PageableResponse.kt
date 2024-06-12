package woowacourse.shopping.remote.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PageableResponse(
    val sort: SortResponse,
    val pageNumber: Int,
    val pageSize: Int,
    val offset: Int,
    val paged: Boolean,
    @SerialName("unpaged") val unPaged: Boolean,
)
