@file:Suppress("UNCHECKED_CAST")

package woowacourse.shopping.domain.result

import woowacourse.shopping.data.remote.api.ApiResponse

inline fun <T : Any?, S : Any?> handleApiResult(
    result: ApiResponse<S>,
    transform: (S) -> T = { it as T },
): Result<T> =
    when (result) {
        is ApiResponse.Success -> Result.Success(transform(result.data))
        is ApiResponse.Error -> handleError(result)
        is ApiResponse.Exception -> Result.Exception(result.e)
    }

fun <T : Any?, S : Any?> handleError(error: ApiResponse.Error<S>): Result<T> =
    when (error.code) {
        401, 403 -> Fail.InvalidAuthorized(error.message)
        404 -> Fail.NotFound(error.message)
        500 -> Fail.Network(error.message)
        else -> Result.Exception(IllegalArgumentException())
    }

inline fun <T : Any?> Result<T>.onSuccess(executable: (T) -> Unit): Result<T> =
    apply {
        if (this is Result.Success) {
            executable(result)
        }
    }

inline fun <T : Any?> Result<T>.onFail(executable: (Fail<T>) -> Unit): Result<T> =
    apply {
        if (this is Fail) {
            executable(this)
        }
    }

inline fun <T : Any?> Result<T>.onException(executable: (Result.Exception<Throwable>) -> Unit): Result<T> =
    apply {
        if (this is Result.Exception) {
            executable(Result.Exception(this.e))
        }
    }
