package woowacourse.shopping.repository.http.exception.product

import woowacourse.shopping.repository.http.common.RemoteException

sealed class ProductRemoteException(
    message: String,
    cause: Throwable? = null,
) : RemoteException(message, cause)

class ProductNetworkException(
    message: String,
    cause: Throwable,
) : ProductRemoteException(message, cause)

class ProductResponseException(
    val code: Int,
    message: String,
) : ProductRemoteException(message)

class ProductParsingException(
    message: String,
    cause: Throwable,
) : ProductRemoteException(message, cause)
