package woowacourse.shopping.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PageableDto(
    @SerialName("offset")
    val offset: Long,
    @SerialName("sort")
    val sort: SortDto,
    @SerialName("paged")
    val paged: Boolean,
    @SerialName("pageNumber")
    val pageNumber: Int,
    @SerialName("pageSize")
    val pageSize: Int,
    @SerialName("unpaged")
    val unpaged: Boolean,
)
