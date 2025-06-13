package woowacourse.shopping.domain.error

sealed interface NetworkError {
    data object Network : NetworkError

    data object Unauthorized : NetworkError

    data object NotFound : NetworkError

    data class Server(
        val code: Int,
    ) : NetworkError

    data class Unknown(
        val throwable: Throwable,
    ) : NetworkError
}
