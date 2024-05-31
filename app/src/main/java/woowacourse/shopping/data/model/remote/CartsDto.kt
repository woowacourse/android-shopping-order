package woowacourse.shopping.data.model.remote

data class CartsDto(
    val content: List<CartDto>,
    val pageableDto: PageableDto,
    val totalElements: Int,
    val last: Boolean,
)
