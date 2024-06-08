package woowacourse.shopping.domain.response

import java.io.IOException

inline fun <T : Any?> Response<T>.resultOrNull(): T? = when (this) {
    is Response.Success -> this.result
    is Response.Exception -> null
    is Fail.Network -> null
    is Fail.NotFound -> null
    is Fail.InvalidAuthorized -> null
}

inline fun <T : Any?> Response<T>.result(): T = when (this) {
    is Response.Success -> this.result
    is Fail.Network -> throw IOException("네트워크 에러가 발생했습니다.")
    is Fail.NotFound -> throw NoSuchElementException("찾을 수 없습니다.")
    is Fail.InvalidAuthorized -> throw IllegalArgumentException("유효하지 않은 인증입니다.")
    is Response.Exception -> throw RuntimeException("에러가 발생했습니다.")
}
