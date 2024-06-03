package woowacourse.shopping.domain.model

data class Carts(
    val totalElements: Int,
    val last: Boolean,
    val pageable: Pageable,
    val content: List<Cart>,
)
