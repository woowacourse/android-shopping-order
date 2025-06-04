package woowacourse.shopping.domain.exception

sealed class NetworkError : Throwable() {
    data object UnknownError : NetworkError()

    sealed class HttpError : NetworkError() {
        data object BadRequestError : HttpError()

        data object AuthenticationError : HttpError()

        data object AuthorizationError : HttpError()

        data object NotFoundError : HttpError()

        data object ServerError : HttpError()
    }
}
