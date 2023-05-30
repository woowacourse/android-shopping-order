package woowacourse.shopping.domain.pagination

interface PrevPagination<T> {
    fun prevItems(callback: (List<T>) -> Unit): List<T>

}
