package woowacourse.shopping.data.repository

import woowacourse.shopping.data.remote.api.ApiResult

fun <T : Any?, S : Any?> handleResponse(
    result: ApiResult<S>,
    transform: (S) -> T
): ApiResponse<out T> =
    when (result) {
        is ApiResult.Success -> ApiResponse.Success(transform(result.data))
        is ApiResult.Error -> handleError(result)
        is ApiResult.Exception -> ApiResponse.Exception(result.e)
    }


fun <T : Any?, S : Any?> handleError(
    error: ApiResult.Error<S>
): Error<T> = when (error.code) {
    401 -> Error.Unauthorized(error.message)
    404 -> Error.NotFound(error.message)
    500 -> Error.Network(error.message)
    else -> Error.Unknown(error.message)
}
inline fun <T : Any?> ApiResponse<T>.onSuccess(
    executable: (T) -> Unit
): ApiResponse<T> = apply {
    if (this is ApiResponse.Success) {
        executable(data)
    }
}

inline fun <T : Any?> ApiResponse<T>.onError(
    executable: (Error<Nothing>) -> Unit
): ApiResponse<T> = apply {
    if (this is Error) {
        executable(this.toErrorNothing())
    }
}

inline fun <T : Any?> Error<T>.toErrorNothing(): Error<Nothing> = when (this) {
    is Error.NotFound -> Error.NotFound(message)
    is Error.Network -> Error.Network(message)
    is Error.Unauthorized -> Error.Unauthorized(message)
    is Error.Unknown -> Error.Unknown(message)
}


inline fun <T : Any?> ApiResponse<T>.onException(
    executable: (ApiResponse.Exception<Throwable>) -> Unit
): ApiResponse<T> = apply {
    if (this is ApiResponse.Exception) {
        executable(ApiResponse.Exception(this.e))
    }
}
