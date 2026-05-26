package woowacourse.shopping.data.remote.cart

import woowacourse.shopping.data.remote.common.RemoteException

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
