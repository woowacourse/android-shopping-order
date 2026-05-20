package woowacourse.shopping.ui.common.error

import retrofit2.HttpException
import java.io.IOException

object ErrorMessageMapper {
    fun toUserMessage(e: Throwable, default: String): String = when (e) {
        is IOException -> "네트워크 연결을 확인해주세요."
        is HttpException -> when (e.code()) {
            401, 403 -> "다시 로그인이 필요해요."
            in 500..599 -> "서버에 일시적 문제가 있어요."
            else -> default
        }

        else -> default
    }
}
