package woowacourse.shopping.data.model

data class PageResult<T>(
    val items: List<T>,
    val currentPage: Int,
    val pageSize: Int,
    val totalCount: Int,
    val totalPages: Int,
) {
    val hasNext: Boolean
        get() = currentPage < totalPages

    val hasPrevious: Boolean
        get() = currentPage > 1
}
