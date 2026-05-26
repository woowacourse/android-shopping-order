package woowacourse.shopping.data.remote.common

abstract class RemoteException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
