package woowacourse.shopping.remote

data class CartItemResponse(
    val totalPages: Int,
    val totalElements: Long,
    val sort: Sort,
    val first: Boolean,
    val last: Boolean,
    val pageable: Pageable,
    val number: Int,
    val numberOfElements: Int,
    val size: Int,
    val content: List<CartItemDto>,
    val empty: Boolean,
)
