package woowacourse.shopping.domain.model

data class Carts(
    val content: List<Cart>,
    val pageable: Pageable,
    val totalElements: Int,
    val last: Boolean,
)
