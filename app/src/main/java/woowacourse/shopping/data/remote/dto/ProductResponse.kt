package woowacourse.shopping.data.remote.dto

data class ProductResponse(
    val content: List<Product>,
    val pageable: Pageable,
    val last: Boolean,
    val totalPages: Int,
    val totalElements: Int,
    val sort: Sort,
    val first: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val size: Int,
    val empty: Boolean
)
