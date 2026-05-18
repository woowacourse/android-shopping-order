package woowacourse.shopping.repository.http.cart

import woowacourse.shopping.repository.http.common.RemoteException

sealed class CartRemoteException(
    message: String,
    cause: Throwable? = null,
) : RemoteException(message, cause)

class CartNetworkException(
    message: String,
    cause: Throwable,
) : CartRemoteException(message, cause)

class CartResponseException(
    val code: Int,
    message: String,
) : CartRemoteException(message)
