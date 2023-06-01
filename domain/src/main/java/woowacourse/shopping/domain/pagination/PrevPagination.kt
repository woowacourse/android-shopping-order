package woowacourse.shopping.domain.pagination

interface PrevPagination<T> {
    fun fetchPrevItems(callback: (List<T>) -> Unit)
}
