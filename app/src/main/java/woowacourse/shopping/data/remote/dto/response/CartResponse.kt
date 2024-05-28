package woowacourse.shopping.data.remote.dto.response

data class CartResponse(
    val totalPages: Int,
    val totalElements: Int,
    val sort: SortDto,
    val first: Boolean,
    val last: Boolean,
    val pageable: PageableDto,
    val number: Int,
    val numberOfElements: Int,
    val size: Int,
    val contentDto: List<ContentDto>,
    val empty: Boolean,
)
