package woowacourse.shopping.repository.http.common

abstract class RemoteException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
