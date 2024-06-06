package woowacourse.shopping.domain.model

data class PageInfo(
    val isPageable: Boolean,
    val currentPage: Int,
    val totalPages: Int,
)
