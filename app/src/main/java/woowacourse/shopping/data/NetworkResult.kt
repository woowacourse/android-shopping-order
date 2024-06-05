package woowacourse.shopping.data

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()

    data object Error : NetworkResult<Nothing>()
}
