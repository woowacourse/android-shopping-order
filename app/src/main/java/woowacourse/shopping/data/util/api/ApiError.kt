package woowacourse.shopping.data.util.api

sealed class ApiError {
    data class Server(
        val code: Int,
        val message: String,
    ) : ApiError()

    object Network : ApiError()

    object NotFound : ApiError()

    object Local : ApiError()
}
