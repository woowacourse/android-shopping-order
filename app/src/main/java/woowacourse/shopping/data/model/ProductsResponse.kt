package woowacourse.shopping.data.model

data class ProductsResponse(
    val content: List<ProductResponse>,
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
