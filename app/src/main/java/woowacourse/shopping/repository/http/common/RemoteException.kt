package woowacourse.shopping.repository.http.common

abstract class RemoteException(
    message: String,
    open val userMessage: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
