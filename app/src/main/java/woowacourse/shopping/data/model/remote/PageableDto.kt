package woowacourse.shopping.data.model.remote

data class PageableDto(
    val sortDto: SortDto,
    val pageNumber: Int,
    val pageSize: Int,
    val offset: Int,
    val paged: Boolean,
    val unPaged: Boolean,
)
