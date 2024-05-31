package woowacourse.shopping.domain.model

data class Carts(
    val content: List<Cart>,
    val pageable: Pageable,
//    val totalPages: Int,
    val totalElements: Int,
//    val sort: Sort,
//    val first: Boolean,
    val last: Boolean,
//    val number: Int,
//    val numberOfElements: Int,
//    val size: Int,
//    val empty: Boolean,
)
