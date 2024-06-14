package woowacourse.shopping.data.dto.response

data class PageableResponse(
    val sort: SortResponse,
    val pageNumber: Int,
    val pageSize: Int,
    val offset: Int,
    val paged: Boolean,
    val unpaged: Boolean,
)
