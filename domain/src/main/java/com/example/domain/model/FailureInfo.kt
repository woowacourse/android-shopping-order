package com.example.domain.model

sealed class FailureInfo(open val message: String) {
    data class Default(
        override val message: String = "Something went wrong :(",
        val throwable: Throwable? = null
    ) : FailureInfo(message)

    data class ClientDefault(
        override val message: String = "4XX Client Error",
        val throwable: Throwable? = null
    ) : FailureInfo(message)

    data class Unauthorized(override val message: String = "Unauthorized Error") :
        FailureInfo(message)

    data class NotFound(override val message: String = "Not Found Error") : FailureInfo(message)

    data class ServerDefault(
        override val message: String = "5XX Server Error",
        val throwable: Throwable? = null
    ) : FailureInfo(message)

    data class GatewayTimeout(override val message: String = "Gateway Timeout") :
        FailureInfo(message)
}
