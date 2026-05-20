package woowacourse.shopping.data.remote.server.apiresult

sealed interface ApiResult<out T> {
    data class Success<T>(val data: T): ApiResult<T>
    data class Error(val code: Int, val message: String?): ApiResult<Nothing>
    data class Exception(val e: Throwable): ApiResult<Nothing>
}