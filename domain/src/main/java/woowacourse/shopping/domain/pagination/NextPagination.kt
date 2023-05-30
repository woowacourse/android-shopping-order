package woowacourse.shopping.domain.pagination

interface NextPagination<T> {
    fun fetchNextItems(callback: (List<T>) -> Unit)
}
