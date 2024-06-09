package woowacourse.shopping.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class PageableResponse(
    val sort: SortResponse,
    val pageNumber: Int,
    val pageSize: Int,
    val offset: Int,
    val paged: Boolean,
    val unpaged: Boolean,
)
