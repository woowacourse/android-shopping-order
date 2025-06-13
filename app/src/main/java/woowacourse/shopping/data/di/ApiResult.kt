package woowacourse.shopping.data.di

sealed class ApiResult<out T> {
    data class Success<T>(
        val data: T,
    ) : ApiResult<T>()

    data class ClientError(
        val code: Int,
        val message: String?,
    ) : ApiResult<Nothing>()

    data class ServerError(
        val code: Int,
        val message: String?,
    ) : ApiResult<Nothing>()

    data class NetworkError(
        val throwable: Throwable,
    ) : ApiResult<Nothing>()

    object UnknownError : ApiResult<Nothing>()
}
