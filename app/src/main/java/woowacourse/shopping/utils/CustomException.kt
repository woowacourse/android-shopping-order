package woowacourse.shopping.utils

sealed class CustomException(message: String) : Exception(message) {
    class NetworkException(message: String) : CustomException(message)
    class ServerException(message: String) : CustomException(message)
    class UnknownException(message: String) : CustomException(message)
}
