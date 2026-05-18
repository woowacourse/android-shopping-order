package woowacourse.shopping.backend.retrofit

import retrofit2.Response

fun <T> Response<T>.bodyOrThrow(errorPrefix: String = "요청 실패"): T {
    throwOnFailure(errorPrefix)

    val body = body()
    if (body == null) {
        throw EmptyBodyException("$errorPrefix: 응답 본문이 비어 있습니다.")
    }

    return body
}

fun Response<*>.throwOnFailure(errorPrefix: String = "요청 실패") {
    if (isSuccessful) return

    throw when (val code = code()) {
        400 -> BadRequestException("$errorPrefix: 잘못된 요청입니다. (400)")
        404 -> NotFoundException("$errorPrefix: 요청한 리소스를 찾을 수 없습니다. (404)")
        in 500..599 -> ServerException("$errorPrefix: 서버 오류가 발생했습니다. ($code)")
        else -> UnknownHttpException("$errorPrefix: HTTP $code")
    }
}
