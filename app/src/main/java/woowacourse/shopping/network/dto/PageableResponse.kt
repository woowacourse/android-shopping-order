package woowacourse.shopping.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class PageableResponse(
    val pageNumber: Int,
    val pageSize: Int,
    val sort: SortResponse,
    val offset: Int,
    val paged: Boolean,
    val unpaged: Boolean
)
