package woowacourse.shopping.retrofit.dto

data class ShoppingCartResponse(
    val content: List<Content>,
    val empty: Boolean,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val pageable: PageableX,
    val size: Int,
    val sort: Sort,
    val totalElements: Long,
    val totalPages: Int
)