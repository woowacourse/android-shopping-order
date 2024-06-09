package woowacourse.shopping.data.remote.api

sealed interface ApiResponse<T : Any?> {
    data class Success<T : Any?>(val data: T) : ApiResponse<T>

    data class Error<T : Any?>(val code: Int, val message: String?) : ApiResponse<T>

    data class Exception<T : Any?>(val e: Throwable) : ApiResponse<T>
}
