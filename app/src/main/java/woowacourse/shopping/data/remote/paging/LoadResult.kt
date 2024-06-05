package woowacourse.shopping.data.remote.paging

sealed interface LoadResult<out T : Any> {
    data class Page<out T : Any>(
        val offset: Int = 0,
        val last: Boolean = false,
        val data: List<T>,
    ) : LoadResult<T>

    data class Error(
        val errorType: LoadErrorType,
    ) : LoadResult<Nothing>
}
