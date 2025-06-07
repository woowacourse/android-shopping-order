package woowacourse.shopping.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PageableResponse(
    @SerialName("offset")
    val offset: Long,
    @SerialName("sort")
    val sort: SortResponse,
    @SerialName("paged")
    val paged: Boolean,
    @SerialName("pageNumber")
    val pageNumber: Int,
    @SerialName("pageSize")
    val pageSize: Int,
    @SerialName("unpaged")
    val unpaged: Boolean,
)
