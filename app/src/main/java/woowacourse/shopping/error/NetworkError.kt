package woowacourse.shopping.error

enum class NetworkError : Error {
    BadRequest,
    Unauthorized,
    Forbidden,
    NotFound,
    InternalServerError,
    IoError,
    UnknownError,
}
