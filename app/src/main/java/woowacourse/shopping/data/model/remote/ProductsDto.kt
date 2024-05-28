package woowacourse.shopping.data.model.remote

data class ProductsDto(
    val content: List<ProductDto>,
    val pageableDto: PageableDto,
    val last: Boolean,
    val totalPages: Int,
    val totalElements: Int,
    val sortDto: SortDto,
    val first: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val size: Int,
    val empty: Boolean,
)
