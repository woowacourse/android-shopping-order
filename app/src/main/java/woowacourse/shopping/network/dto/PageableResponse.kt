package woowacourse.shopping.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class PageableResponse(
    val sort: SortResponse,
    val pageNumber: Int,
    val pageSize: Int,
    val offset: Int,
    val paged: Boolean,
    val unpaged: Boolean
)
