package woowacourse.shopping.feature.common.state

import java.io.IOException

sealed interface AppError {
    data object Network : AppError
    data object Unknown : AppError
}

fun Throwable.toAppError(): AppError = when (this) {
    is IOException -> AppError.Network
    else -> AppError.Unknown
}

fun AppError.toUserMessage(): String = when (this) {
    AppError.Network -> "네트워크 연결을 확인해 주세요."
    AppError.Unknown -> "알 수 없는 오류가 발생했습니다."
}
