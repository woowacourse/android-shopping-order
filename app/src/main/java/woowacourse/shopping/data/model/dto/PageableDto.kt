package woowacourse.shopping.data.model.dto

data class PageableDto(
    val offset: Int,
    val pageNumber: Int,
    val pageSize: Int,
    val paged: Boolean,
    val sort: SortDto,
    val unpaged: Boolean,
)
