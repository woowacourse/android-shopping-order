package woowacourse.shopping.repository.http.exception

import woowacourse.shopping.repository.http.common.RemoteException

sealed class ProductRemoteException(
    message: String,
    override val userMessage: String,
    cause: Throwable? = null,
) : RemoteException(message, userMessage, cause)

class ProductNetworkException(
    message: String,
    userMessage: String = "네트워크 연결 상태가 좋지 않습니다. 확인 후 다시 시도해주세요.",
    cause: Throwable,
) : ProductRemoteException(message, userMessage, cause)

class ProductResponseException(
    val code: Int,
    message: String,
    userMessage: String = "상품 정보를 불러오는 중 서버 오류가 발생했습니다.",
) : ProductRemoteException(message, userMessage)

class ProductParsingException(
    message: String,
    userMessage: String = "상품 정보를 불러오는 중 문제가 발생했습니다.",
    cause: Throwable,
) : ProductRemoteException(message, userMessage, cause)
