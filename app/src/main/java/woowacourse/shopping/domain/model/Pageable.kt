package woowacourse.shopping.domain.model

data class Pageable(
    val sort: Sort,
    val pageNumber: Int,
    val pageSize: Int,
    val offset: Int,
    val paged: Boolean,
    val unPaged: Boolean,
)
