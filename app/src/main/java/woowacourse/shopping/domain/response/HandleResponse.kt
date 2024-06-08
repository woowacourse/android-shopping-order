package woowacourse.shopping.domain.response

import woowacourse.shopping.data.remote.api.ApiResult

inline fun <T : Any?, S : Any?> handleApiResult(
    result: ApiResult<S>,
    transform: (S) -> T = { it as T },
): Response<out T> =
    when (result) {
        is ApiResult.Success -> Response.Success(transform(result.data))
        is ApiResult.Error -> handleError(result)
        is ApiResult.Exception -> Response.Exception(result.e)
    }

inline fun <T : Any?, S : Any?> handleError(error: ApiResult.Error<S>): Response<out T> =
    when (error.code) {
        401, 403 -> Fail.InvalidAuthorized(error.message)
        404 -> Fail.NotFound(error.message)
        500 -> Fail.Network(error.message)
        else -> Response.Exception(IllegalArgumentException())
    }


inline fun <T : Any?> Response<T>.onSuccess(executable: (T) -> Unit): Response<T> =
    apply {
        if (this is Response.Success) {
            executable(result)
        }
    }

inline fun <T : Any?> Response<T>.onFail(executable: (Fail<T>) -> Unit): Response<T> =
    apply {
        if (this is Fail) {
            executable(this)
        }
    }


inline fun <T : Any?> Response<T>.onException(executable: (Response.Exception<Throwable>) -> Unit): Response<T> =
    apply {
        if (this is Response.Exception) {
            executable(Response.Exception(this.e))
        }
    }
