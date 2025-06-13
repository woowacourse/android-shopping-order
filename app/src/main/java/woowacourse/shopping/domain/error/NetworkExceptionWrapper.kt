package woowacourse.shopping.domain.error

data class NetworkExceptionWrapper(
    val networkError: NetworkError,
) : Throwable()
