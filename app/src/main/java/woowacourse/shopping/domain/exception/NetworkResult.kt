package woowacourse.shopping.domain.exception

sealed class NetworkResult<out T> {
    data class Success<T>(val value: T) : NetworkResult<T>()

    data class Error<T>(val exception: NetworkError) : NetworkResult<T>()
}

fun <T> NetworkResult<T>.onSuccess(action: (T) -> Unit): NetworkResult<T> {
    return when (this) {
        is NetworkResult.Success<T> -> {
            action(value)
            this
        }
        is NetworkResult.Error -> this
    }
}

fun <T> NetworkResult<T>.onFailure(action: (NetworkError) -> Unit): NetworkResult<T> {
    return when (this) {
        is NetworkResult.Success<T> -> {
            this
        }
        is NetworkResult.Error<T> -> {
            action(exception)
            this
        }
    }
}
