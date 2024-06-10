package woowacourse.shopping.data.remote

import retrofit2.Response

fun <T : Any?> Response<T>.toApiError(): ApiError {
    val code = code()
    val message = message()
    return when (code) {
        400 -> ApiError.BadRequest(code, message)
        401 -> ApiError.Unauthorized(code, message)
        403 -> ApiError.Forbidden(code, message)
        404 -> ApiError.NotFound(code, message)
        500 -> ApiError.InternalServerError(code, message)
        else -> ApiError.Exception(code, message)
    }
}
