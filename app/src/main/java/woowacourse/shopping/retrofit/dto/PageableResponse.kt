package woowacourse.shopping.retrofit.dto

data class PageableResponse(
    val offset: Long,
    val sort: Sort,
    val paged: Boolean,
    val pageNumber: Int,
    val pageSize: Int,
    val unpaged: Boolean
)