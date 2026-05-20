package woowacourse.shopping.backend.retrofit

import java.io.IOException

sealed class ApiCallException(
    message: String,
) : IOException(message)

class BadRequestException(
    message: String,
) : ApiCallException(message)

class NotFoundException(
    message: String,
) : ApiCallException(message)

class ServerException(
    message: String,
) : ApiCallException(message)

class UnknownHttpException(
    message: String,
) : ApiCallException(message)

class EmptyBodyException(
    message: String,
) : ApiCallException(message)
