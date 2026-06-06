package woowacourse.shopping.data.remote.server.apiresult

sealed interface ApiResult<out T> {
    data class Success<T>(
        val data: T,
    ) : ApiResult<T>

    data class Error(
        val code: Int,
        val message: String?,
    ) : ApiResult<Nothing>

    data class Exception(
        val e: Throwable,
    ) : ApiResult<Nothing>
}

inline fun <T> ApiResult<T>.onSuccess(action: (T) -> Unit): ApiResult<T> {
    if (this is ApiResult.Success) action(data)
    return this
}

inline fun <T> ApiResult<T>.onFailure(action: (code: Int?, message: String?) -> Unit): ApiResult<T> {
    if (this is ApiResult.Error) action(code, message)
    if (this is ApiResult.Exception) action(null, e.message)
    return this
}
