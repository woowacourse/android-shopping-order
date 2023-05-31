package woowacourse.shopping.model

data class PageUiModel(
    val allSize: Int,
    val currentPage: Int
) {
    val pageCount: Int
        get() {
            return kotlin.math.ceil((allSize.toDouble() / PAGE_LOAD_SIZE)).toInt()
        }

    val currentPageStart: Int = (currentPage - 1) * PAGE_LOAD_SIZE

    val currentPageEnd: Int =
        if (currentPageStart + PAGE_LOAD_SIZE >= allSize) allSize
        else currentPageStart + PAGE_LOAD_SIZE

    fun hasPreviousPage(): Boolean {
        return currentPage > 1
    }

    fun hasNextPage(): Boolean {
        return currentPage < pageCount
    }

    fun previousPage(): PageUiModel {
        return this.copy(currentPage = currentPage - 1)
    }

    fun nextPage(): PageUiModel {
        return this.copy(currentPage = currentPage + 1)
    }

    companion object {
        const val PAGE_LOAD_SIZE = 5
    }
}
