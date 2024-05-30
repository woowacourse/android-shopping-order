package woowacourse.shopping.data.remote

sealed interface LoadResult<out T : Any> {
    data class Page<out T : Any>(
        val offset: Int,
        val last: Boolean = false,
        val data: T,
    ) : LoadResult<T>

    data class Error(
        val message: String,
    ) : LoadResult<Nothing>
}
