package woowacourse.shopping.data.model.dto

data class CartItemDto(
    val content: List<Content>,
    val empty: Boolean,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val pageable: Pageable,
    val size: Int,
    val sort: SortDto,
    val totalElements: Int,
    val totalPages: Int,
)
