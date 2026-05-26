package woowacourse.shopping.data.remote.product

import woowacourse.shopping.data.remote.common.RemoteException

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
