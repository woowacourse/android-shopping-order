package woowacourse.shopping.data.remote.retrofit

import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun <T> Call<T>.awaitBody(errorPrefix: String = "요청 실패"): T =
    suspendCancellableCoroutine { continuation ->
        continuation.invokeOnCancellation { cancel() }

        enqueue(
            object : Callback<T> {
                override fun onResponse(
                    call: Call<T>,
                    response: Response<T>,
                ) {
                    if (!response.isSuccessful) {
                        val exception = response.toHttpException(errorPrefix)
                        continuation.resumeWithException(exception)
                        return
                    }

                    val body = response.body()
                    if (body == null) {
                        continuation.resumeWithException(EmptyBodyException("$errorPrefix: 응답 본문이 비어 있습니다."))
                        return
                    }
                    continuation.resume(body)
                }

                override fun onFailure(
                    call: Call<T>,
                    throwable: Throwable,
                ) {
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(throwable)
                }
            },
        )
    }

suspend fun Call<Void>.awaitCompletion(errorPrefix: String = "요청 실패") {
    suspendCancellableCoroutine<Unit> { continuation ->
        continuation.invokeOnCancellation { cancel() }

        enqueue(
            object : Callback<Void> {
                override fun onResponse(
                    call: Call<Void>,
                    response: Response<Void>,
                ) {
                    if (!response.isSuccessful) {
                        val exception = response.toHttpException(errorPrefix)
                        continuation.resumeWithException(exception)
                        return
                    }

                    continuation.resume(Unit)
                }

                override fun onFailure(
                    call: Call<Void>,
                    throwable: Throwable,
                ) {
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(throwable)
                }
            },
        )
    }
}

private fun Response<*>.toHttpException(errorPrefix: String): ApiCallException {
    val code = code()
    val detail = buildHttpErrorDetail()
    return when (code) {
        400 -> BadRequestException("$errorPrefix: 잘못된 요청입니다. (400)$detail")
        404 -> NotFoundException("$errorPrefix: 요청한 리소스를 찾을 수 없습니다. (404)$detail")
        in 500..599 -> ServerException("$errorPrefix: 서버 오류가 발생했습니다. ($code)$detail")
        else -> UnknownHttpException("$errorPrefix: HTTP $code$detail")
    }
}

private fun Response<*>.buildHttpErrorDetail(): String {
    val requestUrl = runCatching { raw().request.url.toString() }.getOrNull().orEmpty()
    val errorBody =
        runCatching { errorBody()?.string()?.trim().orEmpty() }
            .getOrDefault("")
            .take(MAX_ERROR_BODY_LENGTH)

    if (requestUrl.isBlank() && errorBody.isBlank()) return ""

    val details = mutableListOf<String>()
    if (requestUrl.isNotBlank()) details += "url=$requestUrl"
    if (errorBody.isNotBlank()) details += "errorBody=$errorBody"
    return " [${details.joinToString(", ")}]"
}

private const val MAX_ERROR_BODY_LENGTH = 400
