package woowacourse.shopping.data.model.dto

data class ProductResponseDto(
    val content: List<ProductDto>,
    val pageable: PageableDto,
    val last: Boolean,
    val totalPages: Int,
    val totalElements: Int,
    val sort: SortDto,
    val first: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val size: Int,
    val empty: Boolean,
)
