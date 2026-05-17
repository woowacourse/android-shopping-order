package woowacourse.shopping.ui.common.paging

class Pager(
    private val pageSize: Int,
) {
    /**
     * 현재 페이지를 기준으로 다음 페이지가 존재하는지 확인합니다.
     */
    fun hasNext(
        currentPage: Int,
        totalPages: Int,
    ): Boolean = currentPage < totalPages

    /**
     * 현재 페이지를 기준으로 이전 페이지가 존재하는지 확인합니다.
     */
    fun hasPrevious(currentPage: Int): Boolean = currentPage > 1

    /**
     * 현재 크기와 전체 아이템 개수를 기준으로 다음 아이템이 있는지 확인합니다.
     */
    fun canLoadMore(
        currentSize: Int,
        totalSize: Int,
    ): Boolean = currentSize < totalSize
}
