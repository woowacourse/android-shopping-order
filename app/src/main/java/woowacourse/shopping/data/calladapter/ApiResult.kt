package woowacourse.shopping.data.calladapter

sealed class ApiResult<out T> {
    data class Success<T>(
        val data: T,
    ) : ApiResult<T>()

    data class ApiError(
        val code: Int,
        val message: String?,
    ) : ApiResult<Nothing>()

    data object NetworkError : ApiResult<Nothing>()

    data class ClientError(
        val code: Int,
        val message: String?,
    ) : ApiResult<Nothing>()

    data class ServerError(
        val code: Int,
        val message: String?,
    ) : ApiResult<Nothing>()

    data class UnknownError(
        val throwable: Throwable?,
    ) : ApiResult<Nothing>()
}

inline fun <T> ApiResult<T>.onSuccess(action: (T) -> Unit): ApiResult<T> {
    if (this is ApiResult.Success) action(data)
    return this
}

inline fun <T> ApiResult<T>.onClientError(action: (Int, String?) -> Unit): ApiResult<T> {
    if (this is ApiResult.ClientError) action(code, message)
    return this
}

inline fun <T> ApiResult<T>.onServerError(action: (Int, String?) -> Unit): ApiResult<T> {
    if (this is ApiResult.ServerError) action(code, message)
    return this
}
