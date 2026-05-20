package woowacourse.shopping.repository.http.exception

import woowacourse.shopping.repository.http.common.RemoteException

sealed class CartRemoteException(
    message: String,
    override val userMessage: String,
    cause: Throwable? = null,
) : RemoteException(message, userMessage, cause)

class CartNetworkException(
    message: String,
    cause: Throwable,
    userMessage: String = "네트워크 연결 상태가 좋지 않습니다. 확인 후 다시 시도해주세요.",
) : CartRemoteException(message, userMessage, cause)

class CartResponseException(
    val code: Int,
    message: String,
    userMessage: String = "장바구니 처리 중 서버 오류가 발생했습니다.",
) : CartRemoteException(message, userMessage)

class CartParsingException(
    message: String,
    cause: Throwable,
    userMessage: String = "장바구니 데이터를 처리하는 중 오류가 발생했습니다.",
) : CartRemoteException(message, userMessage, cause)
