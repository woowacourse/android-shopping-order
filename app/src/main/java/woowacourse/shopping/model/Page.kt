package woowacourse.shopping.model

data class Page<T>(
    val items: List<T>,
    val isLast: Boolean,
    val totalPages: Int,
    val currentPage: Int,
    val totalElements: Int,
)
