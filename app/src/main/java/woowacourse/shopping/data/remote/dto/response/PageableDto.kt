package woowacourse.shopping.data.remote.dto.response

data class PageableDto(
    val sort: SortDto,
    val pageNumber: Int,
    val pageSize: Int,
    val offset: Int,
    val paged: Boolean,
    val unpaged: Boolean,
)
