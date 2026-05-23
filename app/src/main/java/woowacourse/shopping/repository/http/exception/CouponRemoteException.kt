package woowacourse.shopping.repository.http.exception

import woowacourse.shopping.repository.http.common.RemoteException

sealed class CouponRemoteException(
    message: String,
    override val userMessage: String,
    cause: Throwable? = null,
) : RemoteException(message, userMessage, cause)

class CouponNetworkException(
    message: String,
    cause: Throwable,
    userMessage: String = "네트워크 연결 상태가 좋지 않습니다. 확인 후 다시 시도해주세요.",
) : CouponRemoteException(message, userMessage, cause)

class CouponResponseException(
    val code: Int,
    message: String,
    userMessage: String = "쿠폰 정보를 불러오는 중 서버 오류가 발생했습니다.",
) : CouponRemoteException(message, userMessage)

class CouponParsingException(
    message: String,
    cause: Throwable,
    userMessage: String = "쿠폰 데이터를 처리하는 중 오류가 발생했습니다.",
) : CouponRemoteException(message, userMessage, cause)
