package woowacourse.shopping.data.model.remote

data class CartsDto(
    val content: List<CartDto>,
    val pageable: PageableDto,
    val totalElements: Int,
    val last: Boolean,
)
