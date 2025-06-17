package woowacourse.shopping.data.util.api

sealed class ApiResult<out T> {
    data class Success<T>(
        val data: T,
    ) : ApiResult<T>()

    data class Error(
        val error: ApiError,
    ) : ApiResult<Nothing>()
}
