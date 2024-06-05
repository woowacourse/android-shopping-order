package woowacourse.shopping.data.remote.api

sealed interface ApiResult {
    data class Success(val result: Any?) : ApiResult

    data object Fail : ApiResult

    data class Error(val e: Throwable) : ApiResult
}
