package woowacourse.shopping.data.remote

sealed class ApiError : Exception() {
    abstract val code: Int

    data class BadRequest(override val code: Int, override val message: String) : ApiError()

    data class Unauthorized(override val code: Int, override val message: String) : ApiError()

    data class Forbidden(override val code: Int, override val message: String) : ApiError()

    data class NotFound(override val code: Int, override val message: String) : ApiError()

    data class InternalServerError(override val code: Int, override val message: String) : ApiError()

    data class Exception(override val code: Int, override val message: String) : ApiError()
}
