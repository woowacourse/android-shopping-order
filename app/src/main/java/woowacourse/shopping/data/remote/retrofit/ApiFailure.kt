package woowacourse.shopping.data.remote.retrofit

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

sealed interface ApiFailure {
    data class Http(
        val code: Int,
        val message: String? = null,
    ) : ApiFailure

    data object Timeout : ApiFailure

    data object Network : ApiFailure

    data object Unknown : ApiFailure
}

fun Throwable.toApiFailure(): ApiFailure =
    when (this) {
        is HttpException ->
            ApiFailure.Http(
                code = code(),
                message = message(),
            )

        is SocketTimeoutException -> ApiFailure.Timeout
        is IOException -> ApiFailure.Network
        else -> ApiFailure.Unknown
    }

fun ApiFailure.toUserMessage(defaultMessage: String = "요청 처리 중 오류가 발생했습니다."): String =
    when (this) {
        is ApiFailure.Http ->
            when (code) {
                400 -> "요청 형식이 올바르지 않습니다. (400)"
                401 -> "인증에 실패했습니다. 다시 로그인해 주세요. (401)"
                403 -> "접근 권한이 없습니다. (403)"
                404 -> "요청한 데이터를 찾을 수 없습니다. (404)"
                in 500..599 -> "서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요. ($code)"
                else -> message ?: defaultMessage
            }

        ApiFailure.Timeout -> "요청 시간이 초과되었습니다. 네트워크 상태를 확인해 주세요."
        ApiFailure.Network -> "네트워크 연결을 확인해 주세요."
        ApiFailure.Unknown -> defaultMessage
    }
