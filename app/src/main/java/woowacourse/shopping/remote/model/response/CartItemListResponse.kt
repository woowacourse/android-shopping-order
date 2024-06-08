package woowacourse.shopping.remote.model.response

data class CartItemListResponse(
    val totalPages: Int,
    val totalElements: Long,
    val sort: Sort,
    val first: Boolean,
    val last: Boolean,
    val pageable: Pageable,
    val number: Int,
    val numberOfElements: Int,
    val size: Int,
    val content: List<CartItemResponse>,
    val empty: Boolean,
)
