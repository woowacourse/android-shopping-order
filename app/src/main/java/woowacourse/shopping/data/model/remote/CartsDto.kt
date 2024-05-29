package woowacourse.shopping.data.model.remote

data class CartsDto(
    val totalPages: Int,
    val totalElements: Int,
    val sortDto: SortDto,
    val first: Boolean,
    val last: Boolean,
    val pageableDto: PageableDto,
    val number: Int,
    val numberOfElements: Int,
    val size: Int,
    val content: List<CartDto>,
    val empty: Boolean,
)
