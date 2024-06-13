package woowacourse.shopping.remote

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()

    data class Error(val exception: Throwable = RuntimeException()) : NetworkResult<Nothing>()
}
