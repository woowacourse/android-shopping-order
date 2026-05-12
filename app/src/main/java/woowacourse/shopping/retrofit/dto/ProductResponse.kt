package woowacourse.shopping.retrofit.dto

data class ProductResponse(
    val totalElements: Long,
    val totalPages: Int,
    val size: Int,
    val content: List<Pageable>,
    val number: Int,
    val sort: Sort,
    val pageable: PageableResponse,
    val first: Boolean,
    val last: Boolean,
    val numberOfElements: Int,
    val empty: Boolean
)
